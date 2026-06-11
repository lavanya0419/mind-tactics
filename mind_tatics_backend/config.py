import os

class Config:
    SECRET_KEY = os.environ.get('SECRET_KEY') or 'mind-tatics-super-secret-key-123'
    db_uri = os.environ.get('DATABASE_URL') or 'sqlite:///mindtatics.db'
    if db_uri.startswith("postgres://"):
        db_uri = db_uri.replace("postgres://", "postgresql://", 1)
    SQLALCHEMY_DATABASE_URI = db_uri
    SQLALCHEMY_TRACK_MODIFICATIONS = False
    JWT_SECRET_KEY = os.environ.get('JWT_SECRET_KEY') or 'jwt-secret-key-456'
