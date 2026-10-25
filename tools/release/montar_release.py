"""Monta a release do Aquamarine para jogadores (docs/06-tecnico.md, "Release para jogadores").

Gera build/release/Aquamarine-<versão>/ e o .zip ao lado: instalador de clique duplo (sem Java, sem nada de dev),
os mods (o do Aquamarine e a Fabric API certa), a pasta Servidor pronta e os LEIA-ME. Os modelos dos arquivos
ficam em tools/release/modelo/ ({{VERSAO}}, {{MC}}, {{LOADER}}, {{ICONE}} são trocados aqui).

Uso (na raiz do repositório, depois de ./gradlew build):
    C:\\Python314\\python tools/release/montar_release.py
"""
import base64
import json
import re
import shutil
import sys
import urllib.request
import zipfile
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
MODELO = Path(__file__).resolve().parent / "modelo"
OUT = ROOT / "build" / "release"
CACHE = OUT / "cache"
META = "https://meta.fabricmc.net/v2/versions"


def propriedades():
    props = {}
    for line in (ROOT / "gradle.properties").read_text(encoding="utf-8").splitlines():
        if "=" in line and not line.lstrip().startswith("#"):
            k, v = line.split("=", 1)
            props[k.strip()] = v.strip()
    return props


def baixar(url, destino):
    if destino.exists() and destino.stat().st_size > 0:
        return destino
    destino.parent.mkdir(parents=True, exist_ok=True)
    req = urllib.request.Request(url, headers={"User-Agent": "aquamarine-release"})
    with urllib.request.urlopen(req, timeout=60) as resp:
        destino.write_bytes(resp.read())
    return destino


def fabric_api(versao):
    """O jar agregado da Fabric API: do cache do Gradle, ou do Maven da Fabric."""
    cache = Path.home() / ".gradle" / "caches" / "modules-2" / "files-2.1" / "net.fabricmc.fabric-api" / "fabric-api" / versao
    for jar in cache.glob(f"*/fabric-api-{versao}.jar"):
        return jar
    url = f"https://maven.fabricmc.net/net/fabricmc/fabric-api/fabric-api/{versao}/fabric-api-{versao}.jar".replace("+", "%2B")
    return baixar(url, CACHE / f"fabric-api-{versao}.jar")


def main():
    props = propriedades()
    versao, mc, loader, api = props["version"], props["minecraft_version"], props["loader_version"], props["fabric_api_version"]
    mod_jar = ROOT / "build" / "libs" / f"aquamarine-{versao}.jar"
    if not mod_jar.exists():
        sys.exit(f"Falta {mod_jar}: rode ./gradlew build antes.")

    instalador = json.loads(urllib.request.urlopen(f"{META}/installer", timeout=30).read())
    instalador = next(i["version"] for i in instalador if i.get("stable"))
    servidor_jar = baixar(f"{META}/loader/{mc}/{loader}/{instalador}/server/jar", CACHE / f"fabric-server-{mc}-{loader}-{instalador}.jar")
    perfil = baixar(f"{META}/loader/{mc}/{loader}/profile/json", CACHE / f"fabric-loader-{loader}-{mc}.json")
    perfil_id = json.loads(perfil.read_text(encoding="utf-8"))["id"]
    if perfil_id != f"fabric-loader-{loader}-{mc}":
        sys.exit(f"Perfil do Fabric inesperado: {perfil_id}")
    api_jar = fabric_api(api)
    icone = "data:image/png;base64," + base64.b64encode((ROOT / "src/main/resources/assets/aquamarine/icon.png").read_bytes()).decode()
    troca = {"{{VERSAO}}": versao, "{{MC}}": mc, "{{LOADER}}": loader, "{{ICONE}}": icone}

    pasta = OUT / f"Aquamarine-{versao}"
    if pasta.exists():
        shutil.rmtree(pasta)
    for origem in MODELO.rglob("*"):
        if origem.is_dir():
            continue
        destino = pasta / origem.relative_to(MODELO)
        destino.parent.mkdir(parents=True, exist_ok=True)
        texto = origem.read_text(encoding="utf-8")
        for k, v in troca.items():
            texto = texto.replace(k, v)
        sobra = re.findall(r"\{\{[A-Z_]+\}\}", texto)
        if sobra:
            sys.exit(f"{origem}: marcador sem valor {sobra}")
        if origem.suffix == ".bat":
            # O cmd lê .bat na página de código do console: só ASCII, com CRLF.
            texto.encode("ascii")
            destino.write_bytes(texto.replace("\r\n", "\n").replace("\n", "\r\n").encode("ascii"))
        elif origem.suffix == ".ps1":
            # O PowerShell 5.1 do Windows só lê os acentos como UTF-8 se o arquivo tiver BOM.
            destino.write_text(texto, encoding="utf-8-sig", newline="\r\n")
        elif origem.suffix == ".txt":
            destino.write_text(texto, encoding="utf-8-sig", newline="\r\n")
        else:
            destino.write_text(texto, encoding="utf-8", newline="\n")

    # Uma pasta mods só: o instalador e o servidor copiam dela (o servidor, a cada início: sempre a mesma versão).
    (pasta / "mods").mkdir(parents=True, exist_ok=True)
    for jar in (mod_jar, api_jar):
        shutil.copyfile(jar, pasta / "mods" / jar.name)
    shutil.copyfile(perfil, pasta / "arquivos" / "fabric-perfil.json")
    shutil.copyfile(servidor_jar, pasta / "Servidor" / "fabric-server-launch.jar")

    zip_path = OUT / f"Aquamarine-{versao}.zip"
    if zip_path.exists():
        zip_path.unlink()
    with zipfile.ZipFile(zip_path, "w", zipfile.ZIP_DEFLATED, compresslevel=9) as z:
        for arquivo in sorted(pasta.rglob("*")):
            if arquivo.is_file():
                z.write(arquivo, Path(pasta.name) / arquivo.relative_to(pasta))
    print(f"{zip_path} ({zip_path.stat().st_size / 1e6:.1f} MB): Minecraft {mc}, Fabric Loader {loader}, "
          f"Fabric API {api}, instalador do servidor {instalador}")


if __name__ == "__main__":
    main()
