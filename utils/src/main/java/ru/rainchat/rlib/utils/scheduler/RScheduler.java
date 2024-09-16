package ru.rainchat.rlib.utils.scheduler;

import org.apache.commons.lang3.Validate;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class RScheduler {

    private final JavaPlugin plugin;
    private boolean async;
    private boolean cancel;
    private long after;
    private Long every;

    /**
     * Creates new instance of this class.
     *
     * @param plugin Plugin.
     * @param async  Async.
     */
    public RScheduler(JavaPlugin plugin, boolean async) {
        this.plugin = Objects.requireNonNull(plugin, "plugin cannot be null!");
        this.async = async;
        this.cancel = false;
    }

    /**
     * Checks will scheduler run as async?
     *
     * @return If scheduler run as async, returns true.
     */
    public boolean isAsync() {
        return this.async;
    }

    /**
     * Sets async mode of scheduler.
     *
     * @param async Async mode.
     * @return This class.
     */
    public RScheduler async(boolean async) {
        this.async = async;
        return this;
    }

    /**
     * Runs how many ticks later.
     *
     * @param after Ticks.
     * @return This class.
     */
    public RScheduler after(long after) {
        this.after = after;
        return this;
    }

    /**
     * Runs how many ticks later.
     *
     * @param after Ticks.
     * @return This class.
     */
    public RScheduler addAfter(long after) {
        this.after += after;
        return this;
    }


    /**
     * Runs every how many ticks.
     *
     * @param every Ticks.
     * @return This class.
     */
    public RScheduler every(long every) {
        this.every = every;
        return this;
    }

    /**
     * Checks will scheduler is cancel?
     *
     * @return If scheduler is cancel, returns true.
     */
    public boolean isCancel() {
        return cancel;
    }

    /**
     * Sets calncel mode of scheduler.
     *
     * @return This class.
     */
    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    /**
     * Runs how many ticks later.
     *
     * @param after    Ticks.
     * @param timeUnit Time unit.
     * @return This class.
     */

    public RScheduler after(int after, TimeUnit timeUnit) {
        this.after = timeUnit.toSeconds(after) / 20;
        return this;
    }

    /**
     * Runs every how many ticks.
     *
     * @param every    Ticks.
     * @param timeUnit Time unit.
     * @return This class.
     */

    public RScheduler every(int every, TimeUnit timeUnit) {
        this.every = timeUnit.toSeconds(every) / 20;
        return this;
    }

    /**
     * Starts to scheduler.
     *
     * @param runnable Callback.
     * @return This class.
     */

    public synchronized RScheduler run(Runnable runnable) {
        Validate.notNull(runnable, "runnable cannot be null!");
        return this.run(consumer -> runnable.run());
    }

    /**
     * Starts to scheduler.
     *
     * @param taskConsumer Callback.
     * @return This class.
     */

    public synchronized RScheduler run(Consumer<BukkitRunnable> taskConsumer) {
        Validate.notNull(taskConsumer, "task consumer cannot be null!");

        BukkitRunnable bukkitRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                taskConsumer.accept(this);
            }
        };

        if (cancel) return this;

        if (this.async) {
            if (this.every == null) bukkitRunnable.runTaskLaterAsynchronously(this.plugin, this.after);
            else bukkitRunnable.runTaskTimerAsynchronously(this.plugin, this.after, this.every);
        } else {
            if (this.every == null) bukkitRunnable.runTaskLater(this.plugin, this.after);
            else bukkitRunnable.runTaskTimer(this.plugin, this.after, this.every);
        }

        return this;
    }

}
