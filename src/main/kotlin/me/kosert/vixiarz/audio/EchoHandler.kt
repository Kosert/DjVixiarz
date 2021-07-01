package me.kosert.vixiarz.audio

import net.dv8tion.jda.api.audio.AudioSendHandler
import net.dv8tion.jda.api.audio.AudioReceiveHandler
import java.util.concurrent.ConcurrentLinkedQueue
import net.dv8tion.jda.api.audio.CombinedAudio
import java.nio.ByteBuffer
import java.util.*

class EchoHandler : AudioSendHandler, AudioReceiveHandler {
    /*
            All methods in this class are called by JDA threads when resources are available/ready for processing.
            The receiver will be provided with the latest 20ms of PCM stereo audio
            Note you can receive even while setting yourself to deafened!
            The sender will provide 20ms of PCM stereo audio (pass-through) once requested by JDA
            When audio is provided JDA will automatically set the bot to speaking!
         */
    private val queue: Queue<ByteArray> = ConcurrentLinkedQueue()

    /* Receive Handling */
    // combine multiple user audio-streams into a single one
    override fun canReceiveCombined(): Boolean {
        // limit queue to 10 entries, if that is exceeded we can not receive more until the send system catches up
        return queue.size < 10
    }

    override fun handleCombinedAudio(combinedAudio: CombinedAudio) {
        // we only want to send data when a user actually sent something, otherwise we would just send silence
        if (combinedAudio.users.isEmpty()) return
        val data = combinedAudio.getAudioData(1.0) // volume at 100% = 1.0 (50% = 0.5 / 55% = 0.55)
        queue.add(data)
    }

    /*
        Disable per-user audio since we want to echo the entire channel and not specific users.
        @Override // give audio separately for each user that is speaking
        public boolean canReceiveUser()
        {
            // this is not useful if we want to echo the audio of the voice channel, thus disabled for this purpose
            return false;
        }
        @Override
        public void handleUserAudio(UserAudio userAudio) {} // per-user is not helpful in an echo system
*/
    /* Send Handling */
    override fun canProvide(): Boolean {
        // If we have something in our buffer we can provide it to the send system
        return !queue.isEmpty()
    }

    override fun provide20MsAudio(): ByteBuffer? {
        // use what we have in our buffer to send audio as PCM
        val data = queue.poll()
        return if (data == null) null else ByteBuffer.wrap(data) // Wrap this in a java.nio.ByteBuffer
    }

    override fun isOpus(): Boolean {
        // since we send audio that is received from discord we don't have opus but PCM
        return false
    }
}