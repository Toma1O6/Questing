package dev.toma.questing.common.quest.instance;

import dev.toma.questing.common.component.area.instance.Area;
import dev.toma.questing.common.component.trigger.Trigger;
import net.minecraft.world.World;

public abstract class AbstractAreaQuest extends AbstractQuest implements AreaQuest {

    public AbstractAreaQuest(QuestData questData) {
        super(questData);
    }

    public AbstractAreaQuest() {
    }

    @Override
    protected void onTick(World level) {
        Area area = this.getQuestArea();
        if (area != null) {
            area.onUpdate(level, this);
            if (area.isActiveArea() && area.hasBeenAbandonded()) {
                this.fail(level);
            }
        }
    }

    @Override
    protected <T> boolean shouldAcceptTrigger(Trigger<T> trigger, T triggerData, World level) {
        Area area = this.getQuestArea();
        return area != null && area.isActiveArea();
    }
}
