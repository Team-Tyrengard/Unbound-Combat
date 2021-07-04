package net.havengarde.unbound.combat.combatant;

import net.havengarde.unbound.combat.enums.SkillDisplayType;
import net.havengarde.unbound.combat.skills.UCSkill;

import java.util.HashMap;

public class SkillMatrix extends HashMap<UCSkill, SkillMatrix.SkillData> {
    public byte getLevel(UCSkill skill) {
        return super.getOrDefault(skill, new SkillData()).level;
    }

    public void setLevel(UCSkill skill, byte level) {
        super.compute(skill, (k, v) -> {
            if (v == null)
                v = new SkillData();
            v.level = level;
            return v;
        });
    }

    public SkillDisplayType getDisplayType(UCSkill skill) {
        return super.getOrDefault(skill, new SkillData()).displayType;
    }

    public void setDisplayType(UCSkill skill, SkillDisplayType displayType) {
        super.compute(skill, (k, v) -> {
            if (v == null)
                v = new SkillData();
            v.displayType = displayType;
            return v;
        });
    }

    protected static class SkillData {
        private byte level;
        private SkillDisplayType displayType;

        private SkillData() {
            this.level = 0;
            this.displayType = SkillDisplayType.CHAT_CASTER;
        }
    }
}
