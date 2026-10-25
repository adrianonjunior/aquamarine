package com.aquamarine.client.music;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;
import javax.sound.sampled.AudioFormat;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.JOrbisAudioStream;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.lwjgl.BufferUtils;

/**
 * Misturador de camadas em laço (docs/09-audio.md, "Luta do Kraken"): decodifica as camadas de uma
 * trilha juntas, amostra por amostra, e soma com ganhos que o {@link KrakenMusic} muda ao vivo. Todas as
 * camadas têm o mesmo número de amostras, então voltam ao começo juntas e nunca se desalinham (tocar
 * cada camada num canal próprio do jogo atrasaria umas das outras em dezenas de ms).
 *
 * <p>O jogo pede blocos e enfileira 4; aqui os blocos têm {@link #BLOCK_SECONDS}, então uma mudança de
 * ganho chega ao ouvido em até ~2 s, com rampa de {@link #RAMP_SECONDS}. Fades imediatos (troca de fase,
 * vitória) são feitos pelo volume do {@link LayeredMusicInstance}, que vale no tick seguinte.
 */
public final class LayeredMusicStream implements AudioStream {
	public static final float BLOCK_SECONDS = 0.5F;
	public static final float RAMP_SECONDS = 1.5F;
	private static final int CHANNELS = 2;

	private final Layer[] layers;
	private final AudioFormat format;
	private final int rampFrames;
	private volatile float[] targets;
	private volatile long framesMixed;
	private final float[] gains;

	private LayeredMusicStream(Layer[] layers, AudioFormat format, float[] initial) {
		this.layers = layers;
		this.format = format;
		this.rampFrames = Math.max(1, (int) (format.getSampleRate() * RAMP_SECONDS));
		this.targets = initial.clone();
		this.gains = initial.clone();
	}

	/** Lê os arquivos inteiros (comprimidos, poucos MB) e abre um decodificador por camada. */
	public static LayeredMusicStream open(ResourceProvider resources, List<Identifier> files, float[] initialGains) throws IOException {
		Layer[] layers = new Layer[files.size()];
		AudioFormat format = null;
		for (int i = 0; i < layers.length; i++) {
			byte[] data;
			try (InputStream in = resources.open(files.get(i))) {
				data = in.readAllBytes();
			}
			layers[i] = new Layer(files.get(i), data);
			AudioFormat f = layers[i].decoder.getFormat();
			if (f.getChannels() != CHANNELS) {
				throw new IOException(files.get(i) + ": a camada precisa ser estéreo");
			}
			if (format == null) {
				format = f;
			} else if (f.getSampleRate() != format.getSampleRate()) {
				throw new IOException(files.get(i) + ": taxa de amostragem diferente das outras camadas");
			}
		}
		if (format == null) {
			throw new IOException("trilha sem camadas");
		}
		return new LayeredMusicStream(layers, new AudioFormat(format.getSampleRate(), 16, CHANNELS, true, false), initialGains);
	}

	/** Ganho alvo de cada camada (0 a 1); muda com rampa. Pode ser chamado de qualquer thread. */
	public void setTargets(float[] values) {
		targets = values.clone();
	}

	/** Quadros já entregues ao jogo (para testes: prova que as camadas decodificaram e tocaram). */
	public long framesMixed() {
		return framesMixed;
	}

	@Override
	public AudioFormat getFormat() {
		return format;
	}

	@Override
	public ByteBuffer read(int expectedSize) throws IOException {
		int frames = Math.min(Math.max(expectedSize / (2 * CHANNELS), 1), (int) (format.getSampleRate() * BLOCK_SECONDS));
		for (Layer layer : layers) {
			layer.fill(frames * CHANNELS);
		}
		float[] target = targets;
		float[] step = new float[layers.length];
		for (int i = 0; i < layers.length; i++) {
			step[i] = (target[i] - gains[i]) / rampFrames;
		}
		ByteBuffer out = BufferUtils.createByteBuffer(frames * CHANNELS * 2);
		for (int f = 0; f < frames; f++) {
			for (int c = 0; c < CHANNELS; c++) {
				float sum = 0.0F;
				for (int i = 0; i < layers.length; i++) {
					sum += layers[i].fifo[layers[i].head + f * CHANNELS + c] * gains[i];
				}
				out.putShort((short) Math.round(softClip(sum) * 32767.0F));
			}
			for (int i = 0; i < layers.length; i++) {
				if (step[i] != 0.0F) {
					float next = gains[i] + step[i];
					gains[i] = step[i] > 0 ? Math.min(next, target[i]) : Math.max(next, target[i]);
				}
			}
		}
		for (Layer layer : layers) {
			layer.consume(frames * CHANNELS);
		}
		framesMixed += frames;
		out.flip();
		return out;
	}

	/** Linear até 0,9; acima, curva suave até 1 (as camadas já vêm com pico seguro somadas). */
	static float softClip(float x) {
		float a = Math.abs(x);
		if (a <= 0.9F) {
			return x;
		}
		float over = (a - 0.9F) / 0.1F;
		return Math.copySign(0.9F + 0.1F * (float) Math.tanh(over), x);
	}

	@Override
	public void close() throws IOException {
		for (Layer layer : layers) {
			layer.decoder.close();
		}
	}

	/** Uma camada: decodificador e fila de amostras intercaladas; no fim do arquivo, recomeça. */
	private static final class Layer {
		final Identifier file;
		final byte[] data;
		JOrbisAudioStream decoder;
		float[] fifo = new float[1 << 16];
		int head;
		int size;

		Layer(Identifier file, byte[] data) throws IOException {
			this.file = file;
			this.data = data;
			this.decoder = new JOrbisAudioStream(new ByteArrayInputStream(data));
		}

		void fill(int samples) throws IOException {
			int restarts = 0;
			while (size < samples) {
				if (!decoder.readChunk(this::push)) {
					decoder.close();
					decoder = new JOrbisAudioStream(new ByteArrayInputStream(data));
					if (++restarts > 2) {
						throw new IOException(file + ": camada vazia");
					}
				}
			}
		}

		private void push(float sample) {
			if (head + size >= fifo.length) {
				if (size * 2 > fifo.length) {
					float[] bigger = new float[fifo.length * 2];
					System.arraycopy(fifo, head, bigger, 0, size);
					fifo = bigger;
				} else {
					System.arraycopy(fifo, head, fifo, 0, size);
				}
				head = 0;
			}
			fifo[head + size++] = sample;
		}

		void consume(int samples) {
			head += samples;
			size -= samples;
			if (size == 0) {
				head = 0;
			}
		}
	}
}
