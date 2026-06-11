from flask import Blueprint, request, jsonify
from models import db, User, Score, Progress
from flask_jwt_extended import jwt_required, get_jwt_identity

game_bp = Blueprint('game', __name__)

@game_bp.route('/profile', methods=['GET'])
@jwt_required()
def get_profile():
    user_id = get_jwt_identity()
    user = User.query.get(user_id)
    return jsonify(user.to_dict())

@game_bp.route('/submit-score', methods=['POST'])
@jwt_required()
def submit_score():
    user_id = get_jwt_identity()
    data = request.get_json()
    
    new_score = Score(
        user_id=user_id,
        game_name=data['game_name'],
        score=data['score'],
        level=data['level']
    )
    
    # Update progress if needed
    prog = Progress.query.filter_by(user_id=user_id, game_name=data['game_name']).first()
    if not prog:
        prog = Progress(user_id=user_id, game_name=data['game_name'], level_unlocked=data['level'] + 1)
        db.session.add(prog)
    elif prog.level_unlocked <= data['level']:
        prog.level_unlocked = data['level'] + 1
    
    # Update XP/Level logic (simple)
    user = User.query.get(user_id)
    user.xp += data['score']
    user.level = (user.xp // 1000) + 1
    
    db.session.add(new_score)
    db.session.commit()
    
    return jsonify({"msg": "Score submitted", "user": user.to_dict()}), 200

@game_bp.route('/leaderboard', methods=['GET'])
def get_leaderboard():
    users = User.query.order_by(User.xp.desc()).limit(10).all()
    return jsonify([u.to_dict() for u in users])

@game_bp.route('/progress', methods=['GET'])
@jwt_required()
def get_progress():
    user_id = get_jwt_identity()
    progress = Progress.query.filter_by(user_id=user_id).all()
    return jsonify([{ "game_name": p.game_name, "level_unlocked": p.level_unlocked } for p in progress])
