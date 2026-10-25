"""Registra snapshots imutáveis de modelos e animações; nunca sobrescreve uma versão.

Inicialização feita nesta entrega: python tools/atlas/register_version.py --initial
Nova proposta: python tools/atlas/register_version.py --source modelo.json --label 'Proposta v3'
O JSON pode ser baixado do Atlas e deve ter id e version novos antes de registrar.
"""
import argparse
import json
from datetime import date
from pathlib import Path
from catalog import models

VERSIONS=Path(__file__).resolve().parent/"versions"


def register(record,label):
    import re
    kind,version=record["id"],record["version"]
    if not all(re.fullmatch(r"[a-z0-9_\-]+",v) for v in (kind,version)):
        raise ValueError("ID e versão devem conter apenas letras minúsculas, números, _ e -")
    required=("bones","cubes","clips","texture","resolution")
    if any(key not in record for key in required):
        raise ValueError("Snapshot incompleto")
    VERSIONS.mkdir(exist_ok=True)
    filename=f"{kind}-{version}.json"
    path=VERSIONS/filename
    index_path=VERSIONS/"registry.json"
    index=json.loads(index_path.read_text(encoding="utf-8")) if index_path.exists() else []
    if path.exists() or any(r["id"]==kind and r["version"]==version for r in index):
        raise FileExistsError(f"A versão {kind}/{version} já existe. Crie uma versão nova; o histórico é preservado.")
    snapshot={key:record[key] for key in required}|{"id":kind,"version":version,"format":"aquamarine-atlas-1"}
    path.write_text(json.dumps(snapshot,ensure_ascii=False,separators=(",",":")),encoding="utf-8")
    index.append({"id":kind,"version":version,"label":label,"date":date.today().isoformat(),"file":filename})
    index_path.write_text(json.dumps(index,ensure_ascii=False,indent=2)+"\n",encoding="utf-8")


if __name__=="__main__":
    parser=argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--initial",action="store_true")
    parser.add_argument("--source",type=Path)
    parser.add_argument("--label",default="Proposta visual")
    args=parser.parse_args()
    if args.initial:
        for m in models():
            register(m|{"version":"v1","clips":m["baseline"] if m["id"]=="kraken" else m["clips"]},"Atual no mod · v1")
            if m["proposal"]:
                register(m|m["proposal"]|{"version":"v2"},"Proposta v2 · só no site")
    elif args.source:
        register(json.loads(args.source.read_text(encoding="utf-8")),args.label)
    else:
        parser.error("Use --initial ou --source")
