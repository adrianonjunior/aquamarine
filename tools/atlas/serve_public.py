"""Static-only Atlas server for the public tunnel."""
import argparse
from http.server import ThreadingHTTPServer
from pathlib import Path
from range_handler import RangeRequestHandler


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--port", type=int, default=8767)
    parser.add_argument("--directory", type=Path, default=Path(__file__).resolve().parents[2] / "build/atlas")
    args = parser.parse_args()
    if not (args.directory / "index.html").is_file():
        parser.error(f"Atlas não encontrado em {args.directory}; gere o catálogo antes de abrir o túnel")

    class Handler(RangeRequestHandler):
        def __init__(self, *a, **kw):
            super().__init__(*a, directory=str(args.directory), **kw)

    server = ThreadingHTTPServer(("127.0.0.1", args.port), Handler)
    print(f"Atlas estático: http://127.0.0.1:{args.port}", flush=True)
    server.serve_forever()


if __name__ == "__main__":
    main()
