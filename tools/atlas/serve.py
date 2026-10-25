"""Servidor local opcional: atualização do catálogo sem abrir terminais visíveis."""
import argparse
import json
import threading
from http.server import SimpleHTTPRequestHandler, ThreadingHTTPServer
from pathlib import Path
from urllib.parse import urlparse
from build import ROOT, OUT, build


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--port",type=int,default=8766)
    parser.add_argument("--audio-root",type=Path)
    args = parser.parse_args()
    if not (OUT/"index.html").exists():
        build(args.audio_root)
    lock = threading.Lock()

    class Handler(SimpleHTTPRequestHandler):
        def __init__(self,*a,**kw):
            super().__init__(*a,directory=str(OUT),**kw)

        def end_headers(self):
            self.send_header("Cache-Control","no-store")
            super().end_headers()

        def reply(self,status,obj):
            body = json.dumps(obj).encode()
            self.send_response(status)
            self.send_header("Content-Type","application/json")
            self.send_header("Content-Length",str(len(body)))
            self.end_headers()
            self.wfile.write(body)

        def do_GET(self):
            if self.path == "/api/status":
                self.reply(200,{"app":"aquamarine-atlas","root":str(ROOT)})
            else:
                super().do_GET()

        def do_POST(self):
            if self.path != "/api/refresh":
                self.reply(404,{"error":"Rota desconhecida"});return
            origin = self.headers.get("Origin")
            if origin and urlparse(origin).netloc != self.headers.get("Host"):
                self.reply(403,{"error":"Origem não permitida"});return
            if not lock.acquire(blocking=False):
                self.reply(409,{"error":"Atualização já em andamento"});return
            try:
                data = build(args.audio_root,bundle=False)
                self.reply(200,{"audio":len(data["audio"]),"generated":data["generated"]})
            except Exception as exc:
                self.reply(500,{"error":str(exc)})
            finally:
                lock.release()

    server = ThreadingHTTPServer(("127.0.0.1",args.port),Handler)
    print(f"Aquamarine Atlas: http://127.0.0.1:{args.port}",flush=True)
    server.serve_forever()


if __name__ == "__main__":
    main()
