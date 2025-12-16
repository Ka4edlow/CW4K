from flask import Flask, request, jsonify
from flask_cors import CORS
import pymongo
from cfg import name, aes_key_hex, db_name, collection_name
from Crypto.Cipher import AES
from Crypto.Util.Padding import pad,unpad
from Crypto.Random import get_random_bytes
import binascii
import json
import time

app = Flask(__name__)
app.secret_key = 'supersecretkey'
CORS(app)

client = pymongo.MongoClient(name)
db = client[db_name]
notes_collection = db[collection_name]
notes_collection.create_index([("meta.localId", pymongo.ASCENDING)], unique=True)

AES_KEY = binascii.unhexlify(aes_key_hex)  # 32 bytes

def encrypt_document(doc: dict) -> dict:
    """
    Encrypts JSON document string with AES-CBC, returns dict with iv and ciphertext hex.
    """
    plaintext = json.dumps(doc, ensure_ascii=False).encode('utf-8')
    iv = get_random_bytes(16)
    cipher = AES.new(AES_KEY, AES.MODE_CBC, iv)
    ct = cipher.encrypt(pad(plaintext, AES.block_size))
    return {
        "iv": binascii.hexlify(iv).decode("utf-8"),
        "ciphertext": binascii.hexlify(ct).decode("utf-8"),
        "ts": int(time.time())
    }

@app.route("/health", methods=["GET"])
def health():
    return jsonify({"status": "ok"})

@app.route("/sync_notes", methods=["POST"])
def sync_notes():
    data = request.get_json(force=True)
    notes = data.get("notes", [])
    count = 0
    for n in notes:
        doc = {
            "localId": n.get("localId"),
            "title": n.get("title", ""),
            "description": n.get("description", ""),
            "createdAtMillis": n.get("createdAtMillis", 0),
            "reminder": n.get("reminder", None),
            "isDeleted": n.get("isDeleted", False)
        }
        enc = encrypt_document(doc)
        notes_collection.replace_one(
            {"meta.localId": doc["localId"]},
            {
                "encrypted": enc,
                "meta": {
                    "localId": doc["localId"],
                    "createdAtMillis": doc["createdAtMillis"]
                }
            },
            upsert=True
        )
        count += 1
    return jsonify({"success": True, "syncedCount": count})
    
def decrypt_document(enc: dict) -> dict:
    """
    Розшифровує документ з MongoDB (AES-CBC).
    """
    iv = binascii.unhexlify(enc["iv"])
    ct = binascii.unhexlify(enc["ciphertext"])
    cipher = AES.new(AES_KEY, AES.MODE_CBC, iv)
    pt = unpad(cipher.decrypt(ct), AES.block_size)
    return json.loads(pt.decode("utf-8"))
@app.route("/fetch_notes", methods=["GET"])
def fetch_notes():
    try:
        docs = notes_collection.find()
        notes = []
        for d in docs:
            if "encrypted" in d:
                try:
                    dec = decrypt_document(d["encrypted"])
                    if not dec.get("isDeleted", False):
                        notes.append(dec)
                except Exception as e:
                    print("Decrypt error:", e)
        return jsonify({"success": True, "notes": notes})
    except Exception as e:
        return jsonify({"success": False, "error": str(e)}), 400
    
@app.route("/delete_notes", methods=["POST"])
def delete_notes():
    try:
        data = request.get_json(force=True)
        local_ids = data.get("localIds", [])
        if not local_ids:
            return jsonify({"success": False, "deletedCount": 0}), 400
        res = notes_collection.delete_many({"meta.localId": {"$in": local_ids}})
        return jsonify({"success": True, "deletedCount": res.deleted_count})
    except Exception as e:
        return jsonify({"success": False, "error": str(e)}), 400


    
if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)
