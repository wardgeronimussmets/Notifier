import firebase_admin
from firebase_admin import db
import json
import os
path = os.path.dirname(__file__) + "/"
cred_obj = firebase_admin.credentials.Certificate(path+"privateKeyFirebase.json")
firebase_admin.initialize_app(cred_obj, {
    'databaseURL': 'https://notifier-bc85e-default-rtdb.europe-west1.firebasedatabase.app/'
})

ref = db.reference("/")

print(ref.child("deals").get())