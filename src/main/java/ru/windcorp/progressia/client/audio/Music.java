package ru.windcorp.progressia.client.audio;

import glm.vec._3.Vec3;
import ru.windcorp.progressia.client.audio.backend.Speaker;
import ru.windcorp.progressia.common.util.Namespaced;

public class Music
        extends Namespaced {
    private Vec3 position = new Vec3();
    private Vec3 velocity = new Vec3();
    private float pitch = 1.0f;
    private float gain = 1.0f;


    public Music(String namespace,
                       String name)
    {
        super(namespace, name);
    }

    public Music(String namespace,
                       String name,
                       Vec3 position,
                       Vec3 velocity,
                       float pitch,
                       float gain)
    {
        this(namespace, name);
        this.position = position;
        this.velocity = velocity;
        this.pitch = pitch;
        this.gain = gain;
    }

    public void play(boolean loop)
    {
        Speaker speaker = AudioManager.initMusicSpeaker(this.getId());
        speaker.setGain(gain);
        speaker.setPitch(pitch);
        speaker.setPosition(position);
        speaker.setVelocity(velocity);

        if (loop) {
            speaker.playLoop();
        } else {
            speaker.play();
        }
    }

    //TODO implement
    public void stop() {}

    public void setGain(float gain) {
        this.gain = gain;
    }

    public void setPitch(float pitch) { this.pitch = pitch; }

    public void setVelocity(Vec3 velocity) {
        this.velocity = velocity;
    }

    public Vec3 getPosition() { return position; }

    public float getGain() {
        return gain;
    }

    public Vec3 getVelocity() {
        return velocity;
    }

    public float getPitch() {
        return pitch;
    }
}
