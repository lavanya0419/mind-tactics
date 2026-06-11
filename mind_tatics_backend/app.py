from flask import Flask
from flask_cors import CORS
from flask_jwt_extended import JWTManager
from models import db, bcrypt, User
from config import Config
import os

app = Flask(__name__)
app.config.from_object(Config)

CORS(app)
db.init_app(app)
bcrypt.init_app(app)
jwt = JWTManager(app)

from routes.auth import auth_bp
from routes.game import game_bp

app.register_blueprint(auth_bp, url_prefix='/auth')
app.register_blueprint(game_bp, url_prefix='/api')

@app.route('/')
def home():
    return {"message": "MindTactics Backend is Running!"}, 200


with app.app_context():
    db.create_all()
    # Create test user if not exists
    if not User.query.filter_by(email='test@gmail.com').first():
        hashed_pw = bcrypt.generate_password_hash('123456').decode('utf-8')
        test_user = User(name='Test User', email='test@gmail.com', password=hashed_pw, xp=500, level=1, streak=3)
        db.session.add(test_user)
        db.session.commit()

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)
