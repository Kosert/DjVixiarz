package me.kosert.vixiarz

import discord4j.voice.AudioProvider

class LavaPlayerAudioProvider : AudioProvider() {
    //    private final AudioPlayer player;
    //    private final MutableAudioFrame frame = new MutableAudioFrame();
    //
    //    public LavaPlayerAudioProvider(final AudioPlayer player) {
    //        // Allocate a ByteBuffer for Discord4J's AudioProvider to hold audio data
    //        // for Discord
    //        super(
    //            ByteBuffer.allocate(
    //                StandardAudioDataFormats.DISCORD_OPUS.maximumChunkSize()
    //            )
    //        );
    //        // Set LavaPlayer's MutableAudioFrame to use the same buffer as the one we
    //        // just allocated
    //        frame.setBuffer(getBuffer());
    //        this.player = player;
    //    }
    override fun provide(): Boolean {
        // AudioPlayer writes audio data to its AudioFrame
        //final boolean didProvide = player.provide(frame);
        // If audio was provided, flip from write-mode to read-mode
        //if (didProvide) {
        //    getBuffer().flip();
        //}
        //return didProvide;
        return false
    }
}