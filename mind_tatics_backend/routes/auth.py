from flask import Blueprint, request, jsonify
from models import db, User, bcrypt
from flask_jwt_extended import create_access_token

auth_bp = Blueprint('auth', __name__)

@auth_bp.route('/signup', methods=['POST'])
def signup():
    data = request.get_json()
    if not data or not data.get('email') or not data.get('password') or not data.get('name'):
        return jsonify({"msg": "Missing required fields"}), 400
    
    if User.query.filter_by(email=data['email']).first():
        return jsonify({"msg": "User already exists"}), 400
    
    hashed_password = bcrypt.generate_password_hash(data['password']).decode('utf-8')
    new_user = User(name=data['name'], email=data['email'], password=hashed_password)
    
    db.session.add(new_user)
    db.session.commit()
    
    return jsonify({"msg": "User created successfully"}), 201

@auth_bp.route('/login', methods=['POST'])
def login():
    data = request.get_json()
    user = User.query.filter_by(email=data.get('email')).first()
    
    if user and bcrypt.check_password_hash(user.password, data.get('password')):
        access_token = create_access_token(identity=str(user.id))
        return jsonify(access_token=access_token, user=user.to_dict()), 200
    
    return jsonify({"msg": "Invalid email or password"}), 401
