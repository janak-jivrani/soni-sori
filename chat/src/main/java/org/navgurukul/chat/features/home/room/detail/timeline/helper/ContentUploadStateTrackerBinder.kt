package org.navgurukul.chat.features.home.room.detail.timeline.helper

import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import org.matrix.android.sdk.api.session.content.ContentUploadStateTracker
import org.matrix.android.sdk.api.session.room.send.SendState
import org.navgurukul.chat.R
import org.navgurukul.chat.core.repo.ActiveSessionHolder
import org.navgurukul.chat.core.utils.TextUtils.formatFileSize
import org.navgurukul.chat.features.home.room.detail.timeline.MessageColorProvider
import org.navgurukul.commonui.error.ErrorFormatter

class ContentUploadStateTrackerBinder (private val activeSessionHolder: ActiveSessionHolder,
                                       private val messageColorProvider: MessageColorProvider,
                                       private val errorFormatter: ErrorFormatter
) {

    private val updateListeners = mutableMapOf<String, ContentUploadStateTracker.UpdateListener>()

    fun bind(eventId: String,
             isLocalFile: Boolean,
             progressLayout: ViewGroup
    ) {
        activeSessionHolder.getSafeActiveSession()?.also { session ->
            val uploadStateTracker = session.contentUploadProgressTracker()
            val updateListener = ContentMediaProgressUpdater(progressLayout, isLocalFile, messageColorProvider, errorFormatter)
            updateListeners[eventId] = updateListener
            uploadStateTracker.track(eventId, updateListener)
        }
    }

    fun unbind(eventId: String) {
        activeSessionHolder.getSafeActiveSession()?.also { session ->
            val uploadStateTracker = session.contentUploadProgressTracker()
            updateListeners[eventId]?.also {
                uploadStateTracker.untrack(eventId, it)
            }
        }
    }

    fun clear() {
        activeSessionHolder.getSafeActiveSession()?.also {
            it.contentUploadProgressTracker().clear()
        }
    }
}

private class ContentMediaProgressUpdater(private val progressLayout: ViewGroup,
                                          private val isLocalFile: Boolean,
                                          private val messageColorProvider: MessageColorProvider,
                                          private val errorFormatter: ErrorFormatter) : ContentUploadStateTracker.UpdateListener {

    override fun onUpdate(state: ContentUploadStateTracker.State) {
        when (state) {
            is ContentUploadStateTracker.State.Idle                -> handleIdle()
            is ContentUploadStateTracker.State.EncryptingThumbnail -> handleEncryptingThumbnail()
            is ContentUploadStateTracker.State.UploadingThumbnail  -> handleProgressThumbnail(state)
            is ContentUploadStateTracker.State.Encrypting          -> handleEncrypting()
            is ContentUploadStateTracker.State.Uploading           -> handleProgress(state)
            is ContentUploadStateTracker.State.Failure             -> handleFailure(state)
            is ContentUploadStateTracker.State.Success             -> handleSuccess()
        }
    }

    private fun handleIdle() {
        if (isLocalFile) {
            progressLayout.isVisible = true
            val progressBar = progressLayout.findViewById<ProgressBar>(R.id.mediaProgressBar)
            val progressTextView = progressLayout.findViewById<TextView>(R.id.mediaProgressTextView)
            progressBar?.isVisible = true
            progressBar?.isIndeterminate = true
            progressBar?.progress = 0
            progressTextView?.text = progressLayout.context.getString(R.string.send_file_step_idle)
            progressTextView?.setTextColor(messageColorProvider.getMessageTextColor(SendState.UNSENT))
        } else {
            progressLayout.isVisible = false
        }
    }

    private fun handleEncryptingThumbnail() {
        doHandleEncrypting(R.string.send_file_step_encrypting_thumbnail)
    }

    private fun handleProgressThumbnail(state: ContentUploadStateTracker.State.UploadingThumbnail) {
        doHandleProgress(R.string.send_file_step_sending_thumbnail, state.current, state.total)
    }

    private fun handleEncrypting() {
        doHandleEncrypting(R.string.send_file_step_encrypting_file)
    }

    private fun handleProgress(state: ContentUploadStateTracker.State.Uploading) {
        doHandleProgress(R.string.send_file_step_sending_file, state.current, state.total)
    }

    private fun doHandleEncrypting(resId: Int) {
        progressLayout.visibility = View.VISIBLE
        val progressBar = progressLayout.findViewById<ProgressBar>(R.id.mediaProgressBar)
        val progressTextView = progressLayout.findViewById<TextView>(R.id.mediaProgressTextView)
        progressBar?.isIndeterminate = true
        progressTextView?.text = progressLayout.context.getString(resId)
        progressTextView?.setTextColor(messageColorProvider.getMessageTextColor(SendState.ENCRYPTING))
    }

    private fun doHandleProgress(resId: Int, current: Long, total: Long) {
        progressLayout.visibility = View.VISIBLE
        val percent = 100L * (current.toFloat() / total.toFloat())
        val progressBar = progressLayout.findViewById<ProgressBar>(R.id.mediaProgressBar)
        val progressTextView = progressLayout.findViewById<TextView>(R.id.mediaProgressTextView)
        progressBar?.isVisible = true
        progressBar?.isIndeterminate = false
        progressBar?.progress = percent.toInt()
        progressTextView?.text = progressLayout.context.getString(resId,
                formatFileSize(progressLayout.context, current, true),
                formatFileSize(progressLayout.context, total, true))
        progressTextView?.setTextColor(messageColorProvider.getMessageTextColor(SendState.SENDING))
    }

    private fun handleFailure(state: ContentUploadStateTracker.State.Failure) {
        progressLayout.visibility = View.VISIBLE
        val progressBar = progressLayout.findViewById<ProgressBar>(R.id.mediaProgressBar)
        val progressTextView = progressLayout.findViewById<TextView>(R.id.mediaProgressTextView)
        progressBar?.isVisible = false
        progressTextView?.text = errorFormatter.toHumanReadable(state.throwable)
        progressTextView?.setTextColor(messageColorProvider.getMessageTextColor(SendState.UNDELIVERED))
    }

    private fun handleSuccess() {
        progressLayout.visibility = View.GONE
    }
}