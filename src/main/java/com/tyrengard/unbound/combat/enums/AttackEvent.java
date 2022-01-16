package com.tyrengard.unbound.combat.enums;

public enum AttackEvent {
	HIT, 			SHOOT,
	HIT_CROUCHING, 	SHOOT_CROUCHING,
	HIT_JUMPING, 	SHOOT_JUMPING,
	HIT_FALLING, 	SHOOT_FALLING,
	HIT_SPRINTING;
	
	@Override
	public String toString() {
		switch(this) {
		case HIT: return "Hit";
		case HIT_CROUCHING: return "Hit (Crouching)";
		case HIT_FALLING: return "Hit (Falling)";
		case HIT_JUMPING: return "Hit (Jumping)";
		case HIT_SPRINTING: return "Hit (Sprinting)";
		case SHOOT: return "Shoot";
		case SHOOT_CROUCHING: return "Shoot (Crouching)";
		case SHOOT_FALLING: return "Shoot (Falling)";
		case SHOOT_JUMPING: return "Shoot (Jumping)";
		default: return null;
		}
	}

	public boolean isRanged() { return toString().toLowerCase().startsWith("shoot"); }
	public static AttackEvent[] allHitEvents() { return new AttackEvent[] { HIT, HIT_CROUCHING, HIT_JUMPING, HIT_FALLING, HIT_SPRINTING }; }
	public static AttackEvent[] allShootEvents() {
		return new AttackEvent[] { SHOOT, SHOOT_CROUCHING, SHOOT_JUMPING, SHOOT_FALLING };
	}
}
