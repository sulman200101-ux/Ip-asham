package com.example.ui.viewmodel

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ads.StudioAdManager
import com.example.data.audio.VoiceSynthEngine
import com.example.data.local.StudioDatabase
import com.example.data.local.StudioProjectEntity
import com.example.data.model.AnimationStyle
import com.example.data.model.MusicGenre
import com.example.data.model.ProjectType
import com.example.data.model.SongLyricLine
import com.example.data.model.StoryboardScene
import com.example.data.model.VideoAspectRatio
import com.example.data.model.VocalStyle
import com.example.data.model.VoiceAvatar
import com.example.data.model.VoiceAvatarCatalog
import com.example.data.model.VoiceEmotion
import com.example.data.repository.StudioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StudioViewModel(application: Application) : AndroidViewModel(application) {

    private val db = StudioDatabase.getDatabase(application)
    private val repository = StudioRepository(db.studioDao())
    val voiceEngine = VoiceSynthEngine(application)
    val adManager = StudioAdManager(application)

    val allProjects: StateFlow<List<StudioProjectEntity>> = repository.allProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val credits: StateFlow<Int> = adManager.credits
    val isPlaying: StateFlow<Boolean> = voiceEngine.isPlaying
    val isSingingPlaying: StateFlow<Boolean> = voiceEngine.isSingingPlaying
    val waveAmplitudes: StateFlow<List<Float>> = voiceEngine.currentWaveAmplitudes
    val currentSingingNoteIndex: StateFlow<Int> = voiceEngine.currentSingingNoteIndex

    // ==================== VOICE OVER STATE ====================
    private val _voiceText = MutableStateFlow("مرحباً بكم في استوديو الذكاء الاصطناعي للصوت والغناء والأنيميشن! اكتب أي نص وسأقوم بتحويله إلى أداء صوتي مذهل.")
    val voiceText: StateFlow<String> = _voiceText.asStateFlow()

    private val _selectedAvatar = MutableStateFlow(VoiceAvatarCatalog.avatars[0])
    val selectedAvatar: StateFlow<VoiceAvatar> = _selectedAvatar.asStateFlow()

    private val _selectedEmotion = MutableStateFlow(VoiceEmotion.ENTHUSIASTIC)
    val selectedEmotion: StateFlow<VoiceEmotion> = _selectedEmotion.asStateFlow()

    private val _pitch = MutableStateFlow(1.0f)
    val pitch: StateFlow<Float> = _pitch.asStateFlow()

    private val _speechRate = MutableStateFlow(1.0f)
    val speechRate: StateFlow<Float> = _speechRate.asStateFlow()

    private val _isVoiceGenerating = MutableStateFlow(false)
    val isVoiceGenerating: StateFlow<Boolean> = _isVoiceGenerating.asStateFlow()

    private val _voiceStatusMessage = MutableStateFlow<String?>(null)
    val voiceStatusMessage: StateFlow<String?> = _voiceStatusMessage.asStateFlow()

    // ==================== SINGING & MUSIC STATE ====================
    private val _songTheme = MutableStateFlow("أغنية عن الأحلام والفضاء والإبداع")
    val songTheme: StateFlow<String> = _songTheme.asStateFlow()

    private val _songLyrics = MutableStateFlow("يا ليل النجوم الساطعة\nفي سما الإبداع اللامعة\nنغني بصوت الذكاء وفرحنا\nوالدنيا كلها بتسمعنا!")
    val songLyrics: StateFlow<String> = _songLyrics.asStateFlow()

    private val _selectedGenre = MutableStateFlow(MusicGenre.POP)
    val selectedGenre: StateFlow<MusicGenre> = _selectedGenre.asStateFlow()

    private val _selectedVocalStyle = MutableStateFlow(VocalStyle.POP_STAR)
    val selectedVocalStyle: StateFlow<VocalStyle> = _selectedVocalStyle.asStateFlow()

    private val _isSongGenerating = MutableStateFlow(false)
    val isSongGenerating: StateFlow<Boolean> = _isSongGenerating.asStateFlow()

    private val _songLines = MutableStateFlow<List<SongLyricLine>>(emptyList())
    val songLines: StateFlow<List<SongLyricLine>> = _songLines.asStateFlow()

    // ==================== ANIMATED VIDEO STATE ====================
    private val _videoPrompt = MutableStateFlow("مغامرة روبوت فضائي يكتشف كوكباً مليئاً بالموسيقى والأضواء المتوهجة")
    val videoPrompt: StateFlow<String> = _videoPrompt.asStateFlow()

    private val _selectedAspectRatio = MutableStateFlow(VideoAspectRatio.PORTRAIT_9_16)
    val selectedAspectRatio: StateFlow<VideoAspectRatio> = _selectedAspectRatio.asStateFlow()

    private val _selectedAnimationStyle = MutableStateFlow(AnimationStyle.NEON_PULSE)
    val selectedAnimationStyle: StateFlow<AnimationStyle> = _selectedAnimationStyle.asStateFlow()

    private val _storyboardScenes = MutableStateFlow<List<StoryboardScene>>(emptyList())
    val storyboardScenes: StateFlow<List<StoryboardScene>> = _storyboardScenes.asStateFlow()

    private val _currentSceneIndex = MutableStateFlow(0)
    val currentSceneIndex: StateFlow<Int> = _currentSceneIndex.asStateFlow()

    private val _isVideoGenerating = MutableStateFlow(false)
    val isVideoGenerating: StateFlow<Boolean> = _isVideoGenerating.asStateFlow()

    private val _isVideoPlaying = MutableStateFlow(false)
    val isVideoPlaying: StateFlow<Boolean> = _isVideoPlaying.asStateFlow()

    init {
        // Initialize default song lines and default scenes
        loadSampleSong(MusicGenre.POP)
        _storyboardScenes.value = repository.getFallbackScenes(_videoPrompt.value, AnimationStyle.NEON_PULSE)
    }

    // ==================== VOICE ACTIONS ====================
    fun setVoiceText(text: String) { _voiceText.value = text }
    fun setAvatar(avatar: VoiceAvatar) {
        _selectedAvatar.value = avatar
        _pitch.value = avatar.basePitch * _selectedEmotion.value.pitchModifier
        _speechRate.value = avatar.baseRate * _selectedEmotion.value.rateModifier
    }
    fun setEmotion(emotion: VoiceEmotion) {
        _selectedEmotion.value = emotion
        _pitch.value = _selectedAvatar.value.basePitch * emotion.pitchModifier
        _speechRate.value = _selectedAvatar.value.baseRate * emotion.rateModifier
    }
    fun setPitch(value: Float) { _pitch.value = value }
    fun setSpeechRate(value: Float) { _speechRate.value = value }

    fun generateAiVoiceScript(promptTopic: String) {
        viewModelScope.launch {
            _isVoiceGenerating.value = true
            _voiceStatusMessage.value = "جارِ صياغة النص الإبداعي بواسطة Gemini AI..."
            val result = repository.generateVoiceScript(promptTopic, _selectedEmotion.value, _selectedAvatar.value)
            if (result.isSuccess) {
                _voiceText.value = result.getOrNull() ?: _voiceText.value
                _voiceStatusMessage.value = "تم إنشاء النص بنجاح!"
            } else {
                _voiceStatusMessage.value = result.exceptionOrNull()?.message ?: "حدث خطأ أثناء التوليد"
            }
            _isVoiceGenerating.value = false
        }
    }

    fun playVoice() {
        voiceEngine.speakText(
            text = _voiceText.value,
            pitch = _pitch.value,
            speechRate = _speechRate.value
        )
    }

    fun stopAudio() {
        voiceEngine.stopAll()
        _isVideoPlaying.value = false
    }

    fun saveVoiceProject(title: String, activity: Activity?) {
        viewModelScope.launch {
            val project = StudioProjectEntity(
                title = title.ifBlank { "تعليق صوتي: ${_selectedAvatar.value.nameAr}" },
                description = _voiceText.value.take(60) + "...",
                projectType = ProjectType.VOICE_OVER,
                contentText = _voiceText.value,
                avatarOrGenreId = _selectedAvatar.value.id,
                audioDurationSeconds = (_voiceText.value.length / 15).coerceIn(4, 60)
            )
            repository.saveProject(project)
            adManager.showInterstitial(activity)
        }
    }

    // ==================== SINGING ACTIONS ====================
    fun setSongTheme(theme: String) { _songTheme.value = theme }
    fun setSongLyrics(lyrics: String) { _songLyrics.value = lyrics }
    fun setGenre(genre: MusicGenre) {
        _selectedGenre.value = genre
        loadSampleSong(genre)
    }
    fun setVocalStyle(style: VocalStyle) { _selectedVocalStyle.value = style }

    fun loadSampleSong(genre: MusicGenre) {
        val (lyrics, lines) = repository.getSampleSongLyrics(genre)
        _songLyrics.value = lyrics
        _songLines.value = lines
    }

    fun generateAiSongLyrics(theme: String) {
        viewModelScope.launch {
            _isSongGenerating.value = true
            val result = repository.generateSongLyrics(theme, _selectedGenre.value)
            if (result.isSuccess) {
                val lyrics = result.getOrNull() ?: _songLyrics.value
                _songLyrics.value = lyrics
                // Generate notes from lyrics
                val rawLines = lyrics.lines().filter { it.isNotBlank() }
                val scale = _selectedGenre.value.scaleNotes
                _songLines.value = rawLines.mapIndexed { idx, line ->
                    SongLyricLine(
                        arabicText = line,
                        noteFreq = scale[idx % scale.size],
                        durationMs = (60_000 / _selectedGenre.value.bpm).toLong() * 2
                    )
                }
            }
            _isSongGenerating.value = false
        }
    }

    fun playSingingSong() {
        val notes = if (_songLines.value.isNotEmpty()) {
            _songLines.value.map { it.noteFreq }
        } else {
            _selectedGenre.value.scaleNotes
        }

        voiceEngine.playSingingSong(
            lyrics = _songLyrics.value.lines().filter { it.isNotBlank() },
            notes = notes,
            bpm = _selectedGenre.value.bpm,
            vibratoDepth = _selectedVocalStyle.value.vibrato
        )
    }

    fun saveSongProject(title: String, activity: Activity?) {
        viewModelScope.launch {
            val project = StudioProjectEntity(
                title = title.ifBlank { "أغنية ذكية: ${_selectedGenre.value.titleAr}" },
                description = _songLyrics.value.take(60) + "...",
                projectType = ProjectType.AI_SONG,
                contentText = _songLyrics.value,
                avatarOrGenreId = _selectedGenre.value.name,
                audioDurationSeconds = (_songLines.value.size * 3).coerceIn(10, 120)
            )
            repository.saveProject(project)
            adManager.showInterstitial(activity)
        }
    }

    // ==================== ANIMATED VIDEO ACTIONS ====================
    fun setVideoPrompt(prompt: String) { _videoPrompt.value = prompt }
    fun setAspectRatio(ratio: VideoAspectRatio) { _selectedAspectRatio.value = ratio }
    fun setAnimationStyle(style: AnimationStyle) { _selectedAnimationStyle.value = style }
    fun setCurrentSceneIndex(index: Int) { _currentSceneIndex.value = index }

    fun generateAiStoryboard(prompt: String) {
        viewModelScope.launch {
            _isVideoGenerating.value = true
            val result = repository.generateVideoStoryboard(prompt, _selectedAnimationStyle.value)
            if (result.isSuccess) {
                _storyboardScenes.value = result.getOrNull() ?: _storyboardScenes.value
                _currentSceneIndex.value = 0
            }
            _isVideoGenerating.value = false
        }
    }

    fun toggleVideoPlayback() {
        _isVideoPlaying.value = !_isVideoPlaying.value
        if (_isVideoPlaying.value) {
            playVoice()
        } else {
            stopAudio()
        }
    }

    fun saveVideoProject(title: String, activity: Activity?) {
        viewModelScope.launch {
            val project = StudioProjectEntity(
                title = title.ifBlank { "فيديو متحرك: ${_videoPrompt.value.take(25)}" },
                description = _storyboardScenes.value.firstOrNull()?.textCaption ?: _videoPrompt.value,
                projectType = ProjectType.ANIMATED_VIDEO,
                contentText = _videoPrompt.value,
                avatarOrGenreId = _selectedAnimationStyle.value.name,
                aspectRatioName = _selectedAspectRatio.value.name,
                styleThemeName = _selectedAnimationStyle.value.name,
                audioDurationSeconds = _storyboardScenes.value.size * 4
            )
            repository.saveProject(project)
            adManager.showInterstitial(activity)
        }
    }

    fun deleteProject(id: Long) {
        viewModelScope.launch {
            repository.deleteProject(id)
        }
    }

    fun unlockRewardedCredits(activity: Activity?, onComplete: (Int) -> Unit) {
        adManager.showRewardedAd(activity, onComplete)
    }

    override fun onCleared() {
        super.onCleared()
        voiceEngine.release()
    }
}
