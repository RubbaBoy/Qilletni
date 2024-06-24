/**
 * Gets the ID of the collection. This is an ID identifiable by the provider which owns this collection.
 *
 * @returns[@type string] The ID of the collection
 */
native fun getId() on collection

/**
 * Gets the URL of the collection.
 *
 * @returns[@type string] The URL of the collection
 */
native fun getUrl() on collection

/**
 * Gets the name of the collection.
 *
 * @returns[@type string] The name of the collection
 */
native fun getName() on collection

/**
 * Gets the creator of the collection.
 *
 * @returns[@type core.Artist] The creator of the collection
 */
native fun getCreator() on collection

/**
 * Gets the number of tracks in the collection.
 *
 * @returns[@type int] The number of tracks
 */
native fun getTrackCount() on collection
