/*
    Credit to Exteron for making this for meteor, i ported this from it.
*/

package com.example.modules;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.FactoryRegistry;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.text.Text;
import thunder.hack.events.impl.EventTick;
import thunder.hack.modules.Module;
import thunder.hack.setting.Setting;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;


public class Radio extends Module {
    public final Setting<Integer> Volume = new Setting<>("Volume", 50, 1, 100);
    private final Setting<Radios> Chanel = new Setting("Chanel", Radios.Radio1);


    private AdvancedPlayer player;

    public Radio() {
        super("Radio", Category.getCategory("Radio :D"));
    }

    @Override
    public void onEnable() {
        playRadio();
    }

    @Override
    public void onDisable() {
        stopRadio();
    }

    private void playRadio() {
        try {
            String selectedRadioUrl = Chanel.getValue().URL;
            if (selectedRadioUrl != null) {
                URL radioStream = new URL(selectedRadioUrl);
                player = new AdvancedPlayer(radioStream.openStream(), FactoryRegistry.systemRegistry().createAudioDevice());
                player.setPlayBackListener(new PlaybackListener() {
                    @Override
                    public void playbackFinished(PlaybackEvent evt) {
                        if (evt.getFrame() == Integer.MAX_VALUE) {
                            playRadio();
                        }
                    }
                });

                setVolume(Volume.getValue());

                CompletableFuture.runAsync(() -> {
                    try {
                        player.play();
                    } catch (JavaLayerException e) {
                        e.printStackTrace();
                    }
                });

                mc.player.sendMessage(Text.of("Starting Radio."));
            } else {
                mc.player.sendMessage(Text.of("Selected radio channel URL not found."));
            }
        } catch (IOException | JavaLayerException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    private void onTick(EventTick e) {
        setVolume(Volume.getValue());
    }

    private void setVolume(int volume) {
        Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
        for (Mixer.Info info : mixerInfo) {
            Mixer mixer = AudioSystem.getMixer(info);
            if (!mixer.isLineSupported(Port.Info.SPEAKER)) continue;
            Port port;
            try {
                port = (Port)mixer.getLine(Port.Info.SPEAKER);
                port.open();
            } catch (LineUnavailableException e) {
                mc.player.sendMessage(Text.of("this shouldn't happen..."));
                return;
            }
            if (port.isControlSupported(FloatControl.Type.VOLUME)) {
                FloatControl vol = (FloatControl)port.getControl(FloatControl.Type.VOLUME);
                vol.setValue(volume / 100.0f);
            }
            port.close();
        }
    }

    private void stopRadio() {
        if (player != null) {
            player.close();
            mc.player.sendMessage(Text.of("Radio stopped.")); // im searching for a goofy ah popup for this
            // ThunderHack.notificationManager.publicity("Aura", isRu() ? "Цель успешно нейтрализована!" : "Target successfully neutralized!", 3, Notification.Type.SUCCESS);
        }
    }

    public enum Radios {
        Radio1("https://icast.connectmedia.hu/5202/live.mp3"),
        Custom("file:///C:/Users/user/Downloads/1.mp3"),
        KoronaFM100("https://stream.koronafm100.hu/mp3"),
        RetroRadio("https://icast.connectmedia.hu/5002/live.mp3"),
        PetofiRadio("http://stream002.radio.hu/mr2.mp3"),
        RockFM("https://icast.connectmedia.hu/5301/live.mp3"),
        KossuthRadio("https://icast.connectmedia.hu/4736/mr1.mp3");

        // add custom radios here, if you want a yt video or something like that, download it and put the directory here
        // (u can get the dir by opening the file in chrome)
        public final String URL;

        Radios(String URL) {
            this.URL = URL;
        }
    }


}
