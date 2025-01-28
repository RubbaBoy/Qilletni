// A magic value to represent an unused integer.
int UNUSED_INT = -999

// A magic value to represent an unused double.
double UNUSED_DOUBLE = -999

/**
 * A recommender that generates a list of tracks based on a set of seed artists, genres, or tracks.
 * Various parameters are allowed to fine-tune recommendations. Any unchanged parameters will be ignored.
 * Spotify gives definitions for the "max", "min", and "target" parameters.
 *
 * **A minimum parameter:**
 *
 * A hard ceiling on the selected track attribute's value can be provided. For example, `maxInstrumentalness=0.35` would
 * filter out most tracks that are likely to be instrumental.
 *
 * **A minimum parameter:**
 *
 * A hard floor on the selected track attribute's value can be provided. For example, `minTempo=140` would restrict
 * results to only those tracks with a tempo of greater than 140 beats per minute.
 *
 * **A target parameter:**
 *
 * Tracks with the attribute values nearest to the target values will be preferred. For example, you might request
 * `targetEnergy=0.6` and `targetDanceability=0.8`. All target values will be weighed equally in ranking results.
 */
entity Recommender {

    /**
     * A range from `1-100` of the target number of tracks to recommend.
     */
    int trackLimit = UNUSED_INT
    
    /**
     * A list of artists to seed the recommender with.
     * This is only required if `seedGenres` and `seedTracks` are not set.
     */
    Artist[] seedArtists = []
    
    /**
     * A list of genres to seed the recommender with.
     * This is only required if `seedArtists` and `seedTracks` are not set.
     */
    string[] seedGenres = []
    
    /**
     * A list of tracks to seed the recommender with.
     * This is only required if `seedArtists` and `seedGenres` are not set.
     */
    song[] seedTracks = []
    
    /**
     * A range from `0-1` of a confidence measure from 0.0 to 1.0 of whether the track is acoustic. 1.0 represents high
     * confidence the track is acoustic.
     * See the Recommender entity docs on how to use a `max` parameter.
     */
    double maxAcousticness = UNUSED_DOUBLE
    
    /**
     * A range from `0-1` of a confidence measure from 0.0 to 1.0 of whether the track is acoustic. 1.0 represents high
     * confidence the track is acoustic.
     * See the Recommender entity docs on how to use a `min` parameter.
     */
    double minAcousticness = UNUSED_DOUBLE
    
    /**
     * A range from `0-1` of a confidence measure from 0.0 to 1.0 of whether the track is acoustic. 1.0 represents high
     * confidence the track is acoustic.
     * See the Recommender entity docs on how to use a `target` parameter.
     */
    double targetAcousticness = UNUSED_DOUBLE
    
    /**
     * A range from `0-1` of how suitable a track is for dancing based on a combination of musical elements including
     * tempo, rhythm stability, beat strength, and overall regularity. A value of 0.0 is least danceable and 1.0 is
     * most danceable.
     * See the Recommender entity docs on how to use a `max` parameter.
     */
    double maxDanceability = UNUSED_DOUBLE
    
    /**
     * A range from `0-1` of how suitable a track is for dancing based on a combination of musical elements including
     * tempo, rhythm stability, beat strength, and overall regularity. A value of 0.0 is least danceable and 1.0 is
     * most danceable.
     * See the Recommender entity docs on how to use a `min` parameter.
     */
    double minDanceability = UNUSED_DOUBLE
    
    /**
     * A range from `0-1` of how suitable a track is for dancing based on a combination of musical elements including
     * tempo, rhythm stability, beat strength, and overall regularity. A value of 0.0 is least danceable and 1.0 is
     * most danceable.
     * See the Recommender entity docs on how to use a `target` parameter.
     */
    double targetDanceability = UNUSED_DOUBLE
    
    /**
     * The duration of the track in milliseconds.
     * See the Recommender entity docs on how to use a `max` parameter.
     */
    int maxDurationMs = UNUSED_INT
    
    /**
     * The duration of the track in milliseconds.
     * See the Recommender entity docs on how to use a `min` parameter.
     */
    int minDurationMs = UNUSED_INT
    
    /**
     * The duration of the track in milliseconds.
     * See the Recommender entity docs on how to use a `target` parameter.
     */
    int targetDurationMs = UNUSED_INT
    
    /**
     * A range from `0-1` of a measure from 0.0 to 1.0 and represents a perceptual measure of intensity and activity.
     * Typically, energetic tracks feel fast, loud, and noisy. For example, death metal has high energy, while a Bach
     * prelude scores low on the scale. Perceptual features contributing to this attribute include dynamic range,
     * perceived loudness, timbre, onset rate, and general entropy.
     * See the Recommender entity docs on how to use a `max` parameter.
     */
    double maxEnergy = UNUSED_DOUBLE
    
    /**
     * A range from `0-1` of a measure from 0.0 to 1.0 and represents a perceptual measure of intensity and activity.
     * Typically, energetic tracks feel fast, loud, and noisy. For example, death metal has high energy, while a Bach
     * prelude scores low on the scale. Perceptual features contributing to this attribute include dynamic range,
     * perceived loudness, timbre, onset rate, and general entropy.
     * See the Recommender entity docs on how to use a `min` parameter.
     */
    double minEnergy = UNUSED_DOUBLE
    
    /**
     * A range from `0-1` of a measure from 0.0 to 1.0 and represents a perceptual measure of intensity and activity.
     * Typically, energetic tracks feel fast, loud, and noisy. For example, death metal has high energy, while a Bach
     * prelude scores low on the scale. Perceptual features contributing to this attribute include dynamic range,
     * perceived loudness, timbre, onset rate, and general entropy.
     * See the Recommender entity docs on how to use a `target` parameter.
     */
    double targetEnergy = UNUSED_DOUBLE
    
    /**
     * A range from `0-1` of a prediction whether a track contains no vocals. "Ooh" and "aah" sounds are treated as
     * instrumental in this context. Rap or spoken word tracks are clearly "vocal". The closer the instrumentalness
     * value is to 1.0, the greater likelihood the track contains no vocal content. Values above 0.5 are intended to
     * represent instrumental tracks, but confidence is higher as the value approaches 1.0.
     * See the Recommender entity docs on how to use a `max` parameter.
     */
    double maxInstrumentalness = UNUSED_DOUBLE
    
    /**
     * A range from `0-1` of a prediction whether a track contains no vocals. "Ooh" and "aah" sounds are treated as
     * instrumental in this context. Rap or spoken word tracks are clearly "vocal". The closer the instrumentalness
     * value is to 1.0, the greater likelihood the track contains no vocal content. Values above 0.5 are intended to
     * represent instrumental tracks, but confidence is higher as the value approaches 1.0.
     * See the Recommender entity docs on how to use a `min` parameter.
     */
    double minInstrumentalness = UNUSED_DOUBLE
    
    /**
     * A range from `0-1` of a prediction whether a track contains no vocals. "Ooh" and "aah" sounds are treated as
     * instrumental in this context. Rap or spoken word tracks are clearly "vocal". The closer the instrumentalness
     * value is to 1.0, the greater likelihood the track contains no vocal content. Values above 0.5 are intended to
     * represent instrumental tracks, but confidence is higher as the value approaches 1.0.
     * See the Recommender entity docs on how to use a `target` parameter.
     */
    double targetInstrumentalness = UNUSED_DOUBLE
    
    /**
     * A range from `-1-11` of the key the track is in. Integers map to pitches using standard Pitch Class notation.
     * E.g. 0 = C, 1 = C♯/D♭, 2 = D, and so on. If no key was detected, the value is -1.
     * See the Recommender entity docs on how to use a `max` parameter.
     */
    int maxKey = UNUSED_INT
    
    /**
     * A range from `-1-11` of the key the track is in. Integers map to pitches using standard Pitch Class notation.
     * E.g. 0 = C, 1 = C♯/D♭, 2 = D, and so on. If no key was detected, the value is -1.
     * See the Recommender entity docs on how to use a `min` parameter.
     */
    int minKey = UNUSED_INT
    
    /**
     * A range from `-1-11` of the key the track is in. Integers map to pitches using standard Pitch Class notation.
     * E.g. 0 = C, 1 = C♯/D♭, 2 = D, and so on. If no key was detected, the value is -1.
     * See the Recommender entity docs on how to use a `target` parameter.
     */
    int targetKey = UNUSED_INT
    
    /**
     * A range from `0-1` of the presence of an audience in the recording. Higher liveness values represent an
     * increased probability that the track was performed live. A value above 0.8 provides strong likelihood that the
     * track is live.
     * See the Recommender entity docs on how to use a `max` parameter.
     */
    double maxLiveness = UNUSED_DOUBLE
    
    /**
     * A range from `0-1` of the presence of an audience in the recording. Higher liveness values represent an
     * increased probability that the track was performed live. A value above 0.8 provides strong likelihood that the
     * track is live.
     * See the Recommender entity docs on how to use a `min` parameter.
     */
    double minLiveness = UNUSED_DOUBLE
    
    /**
     * A range from `0-1` of the presence of an audience in the recording. Higher liveness values represent an
     * increased probability that the track was performed live. A value above 0.8 provides strong likelihood that the
     * track is live.
     * See the Recommender entity docs on how to use a `target` parameter.
     */
    double targetLiveness = UNUSED_DOUBLE
    
    /**
     * A value of `0` or `1` indicating the modality (major or minor) of a track, the type of scale from which its
     * melodic content is derived. Major is represented by 1 and minor is 0.
     * See the Recommender entity docs on how to use a `max` parameter.
     */
    int maxMode = UNUSED_INT
    
    /**
     * A value of `0` or `1` indicating the modality (major or minor) of a track, the type of scale from which its
     * melodic content is derived. Major is represented by 1 and minor is 0.
     * See the Recommender entity docs on how to use a `min` parameter.
     */
    int minMode = UNUSED_INT
    
    /**
     * A value of `0` or `1` indicating the modality (major or minor) of a track, the type of scale from which its
     * melodic content is derived. Major is represented by 1 and minor is 0.
     * See the Recommender entity docs on how to use a `target` parameter.
     */
    int targetMode = UNUSED_INT
    
    /**
     * A range from `0-100` of the popularity of the track, with 100 being the most popular. The popularity is
     * calculated by algorithm and is based, in the most part, on the total number of plays the track has had and how
     * recent those plays are. Generally speaking, songs that are being played a lot now will have a higher popularity
     * than songs that were played a lot in the past. Duplicate tracks (e.g. the same track from a single and an album)
     * are rated independently. Artist and album popularity is derived mathematically from track popularity. 
     * See the Recommender entity docs on how to use a `max` parameter.
     */
    int maxPopularity = UNUSED_INT
    
    /**
     * A range from `0-100` of the popularity of the track, with 100 being the most popular. The popularity is
     * calculated by algorithm and is based, in the most part, on the total number of plays the track has had and how
     * recent those plays are. Generally speaking, songs that are being played a lot now will have a higher popularity
     * than songs that were played a lot in the past. Duplicate tracks (e.g. the same track from a single and an album)
     * are rated independently. Artist and album popularity is derived mathematically from track popularity. 
     * See the Recommender entity docs on how to use a `min` parameter.
     */
    int minPopularity = UNUSED_INT
    
    /**
     * A range from `0-100` of the popularity of the track, with 100 being the most popular. The popularity is
     * calculated by algorithm and is based, in the most part, on the total number of plays the track has had and how
     * recent those plays are. Generally speaking, songs that are being played a lot now will have a higher popularity
     * than songs that were played a lot in the past. Duplicate tracks (e.g. the same track from a single and an album)
     * are rated independently. Artist and album popularity is derived mathematically from track popularity. 
     * See the Recommender entity docs on how to use a `target` parameter.
     */
    int targetPopularity = UNUSED_INT

    /**
     * A range from `0-1` of the presence of spoken words in a track. The more exclusively speech-like the recording
     * (e.g. talk show, audio book, poetry), the closer to 1.0 the attribute value. Values above 0.66 describe tracks
     * that are probably made entirely of spoken words. Values between 0.33 and 0.66 describe tracks that may contain
     * both music and speech, either in sections or layered, including such cases as rap music. Values below 0.33 most
     * likely represent music and other non-speech-like tracks.
     * See the Recommender entity docs on how to use a `max` parameter.
     */
    double maxSpeechiness = UNUSED_DOUBLE
    
    /**
     * A range from `0-1` of the presence of spoken words in a track. The more exclusively speech-like the recording
     * (e.g. talk show, audio book, poetry), the closer to 1.0 the attribute value. Values above 0.66 describe tracks
     * that are probably made entirely of spoken words. Values between 0.33 and 0.66 describe tracks that may contain
     * both music and speech, either in sections or layered, including such cases as rap music. Values below 0.33 most
     * likely represent music and other non-speech-like tracks.
     * See the Recommender entity docs on how to use a `min` parameter.
     */
    double minSpeechiness = UNUSED_DOUBLE
    
    /**
     * A range from `0-1` of the presence of spoken words in a track. The more exclusively speech-like the recording
     * (e.g. talk show, audio book, poetry), the closer to 1.0 the attribute value. Values above 0.66 describe tracks
     * that are probably made entirely of spoken words. Values between 0.33 and 0.66 describe tracks that may contain
     * both music and speech, either in sections or layered, including such cases as rap music. Values below 0.33 most
     * likely represent music and other non-speech-like tracks.
     * See the Recommender entity docs on how to use a `target` parameter.
     */
    double targetSpeechiness = UNUSED_DOUBLE
    
    /**
     * The overall estimated tempo of a track in beats per minute (BPM). In musical terminology, tempo is the speed or
     * pace of a given piece and derives directly from the average beat duration.
     * See the Recommender entity docs on how to use a `max` parameter.
     */
    double maxTempo = UNUSED_DOUBLE
    
    /**
     * The overall estimated tempo of a track in beats per minute (BPM). In musical terminology, tempo is the speed or
     * pace of a given piece and derives directly from the average beat duration.
     * See the Recommender entity docs on how to use a `min` parameter.
     */
    double minTempo = UNUSED_DOUBLE
    
    /**
     * The overall estimated tempo of a track in beats per minute (BPM). In musical terminology, tempo is the speed or
     * pace of a given piece and derives directly from the average beat duration.
     * See the Recommender entity docs on how to use a `target` parameter.
     */
    double targetTempo = UNUSED_DOUBLE
    
    /**
     * A range between `3-7` of the estimated time signature. The time signature (meter) is a notational convention to
     * specify how many beats are in each bar (or measure). The time signature ranges from 3 to 7 indicating time
     * signatures of "3/4", to "7/4".
     * See the Recommender entity docs on how to use a `max` parameter.
     */
    int maxTimeSignature = UNUSED_INT
    
    /**
     * A range between `3-7` of the estimated time signature. The time signature (meter) is a notational convention to
     * specify how many beats are in each bar (or measure). The time signature ranges from 3 to 7 indicating time
     * signatures of "3/4", to "7/4".
     * See the Recommender entity docs on how to use a `min` parameter.
     */
    int minTimeSignature = UNUSED_INT
    
    /**
     * A range between `3-7` of the estimated time signature. The time signature (meter) is a notational convention to
     * specify how many beats are in each bar (or measure). The time signature ranges from 3 to 7 indicating time
     * signatures of "3/4", to "7/4".
     * See the Recommender entity docs on how to use a `target` parameter.
     */
    int targetTimeSignature = UNUSED_INT
    
    /**
     * A range between `0-1` describing the musical positiveness conveyed by a track. Tracks with high valence sound
     * more positive (e.g. happy, cheerful, euphoric), while tracks with low valence sound more negative (e.g. sad,
     * depressed, angry).
     * See the Recommender entity docs on how to use a `max` parameter.
     */
    double maxValence = UNUSED_DOUBLE
    
    /**
     * A range between `0-1` describing the musical positiveness conveyed by a track. Tracks with high valence sound
     * more positive (e.g. happy, cheerful, euphoric), while tracks with low valence sound more negative (e.g. sad,
     * depressed, angry).
     * See the Recommender entity docs on how to use a `min` parameter.
     */
    double minValence = UNUSED_DOUBLE
    
    /**
     * A range between `0-1` describing the musical positiveness conveyed by a track. Tracks with high valence sound
     * more positive (e.g. happy, cheerful, euphoric), while tracks with low valence sound more negative (e.g. sad,
     * depressed, angry).
     * See the Recommender entity docs on how to use a `target` parameter.
     */
    double targetValence = UNUSED_DOUBLE
    
    /**
     * Returns a list of songs that are recommended based on properties of the entity.
     *
     * @returns[@type list] A list of recommended songs
     */
    fun recommend() {
        return recommend(trackLimit)
    }
    
    /**
     * Returns a list of songs that are recommended based on properties of the entity.
     *
     * @param trackLimitOverride A range from `1-100` of the target number of tracks to recommend, overriding the
     *                           entity's `trackLimit` field.
     * @returns[@type list] A list of recommended songs
     */
    native fun recommend(trackLimitOverride)
}
