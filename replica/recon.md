# Clean-Room Reverse Engineering Reconnaissance Map

## 1. Screen Inventory & UX Map
1. **Home / Feed Screen**: Displays categorized carousels (Trending, Recent, History) fetched across enabled provider extensions.
2. **Search Screen**: Global multi-query orchestrator with debounced search and per-provider result streams.
3. **Media Details Screen**: Poster artwork, overview synopsis, bookmark status, and episodic track list with download actions.
4. **Player Screen (ExoPlayer)**: Custom OSD controls, gesture-based brightness/volume controls, multi-track audio selection, subtitle synchronization.
5. **Downloads Screen**: Active progress list (WorkManager), pause/resume handlers, and local disk space indicators.
6. **Extensions / Repositories Screen**: Add community repo URLs, toggle installed providers, manage plugin updates.

## 2. Inferred Data Model
- **ProviderSource**: ID, display name, base URL, API version, source capabilities (Anime/Movie/Series/IPTV).
- **WatchHistory**: Composite key (`provider_id + media_id + episode_id`), playback offset (ms), total duration (ms), timestamp.
- **DownloadTask**: Unique task ID, media reference, remote stream URL, local storage path, progress bytes, and lifecycle status.
