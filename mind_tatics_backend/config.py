import os

class Config:
    SECRET_KEY = os.environ.get('SECRET_KEY') or 'mind-tatics-super-secret-key-123'
    SQLALCHEMY_DATABASE_URI = 'sqlite:///mindtatics.db'
    SQLALCHEMY_TRACK_MODIFICATIONS = False
    JWT_SECRET_KEY = os.environ.get('JWT_SECRET_KEY') or 'jwt-secret-key-456'
