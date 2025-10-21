// Sample music data
const sampleSongs = [
    {
        id: 1,
        title: "Blinding Lights",
        artist: "The Weeknd",
        album: "After Hours",
        duration: "3:20",
        audioUrl: "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        coverUrl: "https://via.placeholder.com/300",
        liked: false,
        downloaded: false
    },
    {
        id: 2,
        title: "Save Your Tears",
        artist: "The Weeknd",
        album: "After Hours",
        duration: "3:35",
        audioUrl: "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
        coverUrl: "https://via.placeholder.com/300",
        liked: false,
        downloaded: false
    },
    {
        id: 3,
        title: "Levitating",
        artist: "Dua Lipa",
        album: "Future Nostalgia",
        duration: "3:23",
        audioUrl: "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
        coverUrl: "https://via.placeholder.com/300",
        liked: false,
        downloaded: false
    },
    {
        id: 4,
        title: "Stay",
        artist: "The Kid LAROI, Justin Bieber",
        album: "F*CK LOVE 3",
        duration: "2:21",
        audioUrl: "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3",
        coverUrl: "https://via.placeholder.com/300",
        liked: false,
        downloaded: false
    },
    {
        id: 5,
        title: "Good 4 U",
        artist: "Olivia Rodrigo",
        album: "SOUR",
        duration: "2:58",
        audioUrl: "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3",
        coverUrl: "https://via.placeholder.com/300",
        liked: false,
        downloaded: false
    }
];

// Initialize the application
document.addEventListener('DOMContentLoaded', function () {
    // Check if we're on the login page
    if (document.querySelector('.login-page')) {
        initLoginPage();
    } else {
        // Initialize the music player for all other pages
        initMusicPlayer();

        // Load user data and playlists
        loadUserData();

        // Page-specific initialization
        if (document.querySelector('body').id === 'home-page') {
            initHomePage();
        } else if (document.querySelector('body').id === 'search-page') {
            initSearchPage();
        } else if (document.querySelector('body').id === 'library-page') {
            initLibraryPage();
        } else if (document.querySelector('body').id === 'playlist-page') {
            initPlaylistPage();
        }
    }
});

// Login Page Functions
function initLoginPage() {
    const loginForm = document.getElementById('loginForm');
    const registerForm = document.getElementById('registerForm');

    // Load sample user data if not exists
    if (!localStorage.getItem('users')) {
        const sampleUsers = [
            {
                email: 'testuser@gmail.com',
                mobile: '0123456789',
                password: '12345',
                likedSongs: [],
                downloadedSongs: [],
                playlists: []
            }
        ];
        localStorage.setItem('users', JSON.stringify(sampleUsers));
    }

    // Login form submission
    loginForm.addEventListener('submit', function (e) {
        e.preventDefault();
        const email = document.getElementById('loginEmail').value;
        const password = document.getElementById('loginPassword').value;

        const users = JSON.parse(localStorage.getItem('users'));
        const user = users.find(u =>
            (u.email === email || u.mobile === email) && u.password === password
        );

        if (user) {
            // Store current user in session
            localStorage.setItem('currentUser', JSON.stringify(user));
            window.location.href = 'home.html';
        } else {
            alert('Invalid credentials. Please try again.');
        }
    });

    // Register form submission
    registerForm.addEventListener('submit', function (e) {
        e.preventDefault();
        const email = document.getElementById('regEmail').value;
        const mobile = document.getElementById('regMobile').value;
        const password = document.getElementById('regPassword').value;

        const users = JSON.parse(localStorage.getItem('users'));

        // Check if email already exists
        if (users.some(u => u.email === email)) {
            alert('Email already registered. Please use another email or login.');
            return;
        }

        // Add new user
        const newUser = {
            email,
            mobile,
            password,
            likedSongs: [],
            downloadedSongs: [],
            playlists: []
        };

        users.push(newUser);
        localStorage.setItem('users', JSON.stringify(users));

        // Store current user in session
        localStorage.setItem('currentUser', JSON.stringify(newUser));
        window.location.href = 'home.html';
    });

    // Google login button (simulated)
    document.getElementById('googleLoginBtn').addEventListener('click', function () {
        const users = JSON.parse(localStorage.getItem('users'));
        const user = users.find(u => u.email === 'testuser@gmail.com');

        if (user) {
            localStorage.setItem('currentUser', JSON.stringify(user));
            window.location.href = 'home.html';
        }
    });
}

// Music Player Functions
function initMusicPlayer() {
    const audioPlayer = document.getElementById('audioPlayer');
    const playBtn = document.getElementById('playBtn');
    const prevBtn = document.getElementById('prevBtn');
    const nextBtn = document.getElementById('nextBtn');
    const likeBtn = document.getElementById('likeBtn');
    const downloadBtn = document.getElementById('downloadBtn');
    const progressBar = document.querySelector('.progress-bar');
    const currentTimeEl = document.querySelector('.current-time');
    const totalTimeEl = document.querySelector('.total-time');
    const volumeSlider = document.getElementById('volumeSlider');

    let currentSongIndex = 0;
    let isPlaying = false;

    // Load the first song by default
    loadSong(currentSongIndex);

    // Play/Pause button
    playBtn.addEventListener('click', function () {
        if (isPlaying) {
            pauseSong();
        } else {
            playSong();
        }
    });

    // Previous button
    prevBtn.addEventListener('click', function () {
        currentSongIndex--;
        if (currentSongIndex < 0) {
            currentSongIndex = sampleSongs.length - 1;
        }
        loadSong(currentSongIndex);
        playSong();
    });

    // Next button
    nextBtn.addEventListener('click', function () {
        currentSongIndex++;
        if (currentSongIndex > sampleSongs.length - 1) {
            currentSongIndex = 0;
        }
        loadSong(currentSongIndex);
        playSong();
    });

    // Like button
    likeBtn.addEventListener('click', function () {
        toggleLike();
    });

    // Download button
    downloadBtn.addEventListener('click', function () {
        downloadSong();
    });

    // Progress bar click
    document.querySelector('.progress').addEventListener('click', function (e) {
        const percent = e.offsetX / this.offsetWidth;
        audioPlayer.currentTime = percent * audioPlayer.duration;
    });

    // Volume control
    volumeSlider.addEventListener('input', function () {
        audioPlayer.volume = this.value / 100;
    });

    // Update progress bar
    audioPlayer.addEventListener('timeupdate', function () {
        const currentTime = audioPlayer.currentTime;
        const duration = audioPlayer.duration;
        const percent = (currentTime / duration) * 100;

        progressBar.style.width = `${percent}%`;

        // Update time display
        currentTimeEl.textContent = formatTime(currentTime);
        totalTimeEl.textContent = formatTime(duration);
    });

    // Song ended
    audioPlayer.addEventListener('ended', function () {
        nextBtn.click();
    });

    // Format time (seconds to MM:SS)
    function formatTime(seconds) {
        const minutes = Math.floor(seconds / 60);
        const secs = Math.floor(seconds % 60);
        return `${minutes}:${secs < 10 ? '0' : ''}${secs}`;
    }

    // Load song
    function loadSong(index) {
        const song = sampleSongs[index];

        // Update UI
        document.querySelector('.song-title').textContent = song.title;
        document.querySelector('.artist-name').textContent = song.artist;
        document.querySelector('.album-cover').src = song.coverUrl;

        // Set audio source
        audioPlayer.src = song.audioUrl;

        // Update like button
        updateLikeButton(song.liked);
    }

    // Play song
    function playSong() {
        audioPlayer.play();
        isPlaying = true;
        playBtn.innerHTML = '<i class="bi bi-pause-fill"></i>';
    }

    // Pause song
    function pauseSong() {
        audioPlayer.pause();
        isPlaying = false;
        playBtn.innerHTML = '<i class="bi bi-play-fill"></i>';
    }

    // Toggle like
    function toggleLike() {
        const currentUser = JSON.parse(localStorage.getItem('currentUser'));
        const currentSong = sampleSongs[currentSongIndex];

        if (currentSong.liked) {
            currentSong.liked = false;
            // Remove from liked songs
            currentUser.likedSongs = currentUser.likedSongs.filter(id => id !== currentSong.id);
        } else {
            currentSong.liked = true;
            // Add to liked songs
            if (!currentUser.likedSongs.includes(currentSong.id)) {
                currentUser.likedSongs.push(currentSong.id);
            }
        }

        // Save changes
        localStorage.setItem('currentUser', JSON.stringify(currentUser));
        updateLikeButton(currentSong.liked);
    }

    // Update like button
    function updateLikeButton(liked) {
        if (liked) {
            likeBtn.innerHTML = '<i class="bi bi-heart-fill"></i>';
            likeBtn.classList.add('liked');
        } else {
            likeBtn.innerHTML = '<i class="bi bi-heart"></i>';
            likeBtn.classList.remove('liked');
        }
    }

    // Download song
    function downloadSong() {
        const currentUser = JSON.parse(localStorage.getItem('currentUser'));
        const currentSong = sampleSongs[currentSongIndex];

        if (currentSong.downloaded) {
            alert('Song already downloaded!');
            return;
        }

        currentSong.downloaded = true;

        // Add to downloaded songs
        if (!currentUser.downloadedSongs.includes(currentSong.id)) {
            currentUser.downloadedSongs.push(currentSong.id);
        }

        // Save changes
        localStorage.setItem('currentUser', JSON.stringify(currentUser));
        alert('Song downloaded successfully!');
    }
}

// Load user data and playlists
function loadUserData() {
    const currentUser = JSON.parse(localStorage.getItem('currentUser'));
    if (!currentUser) {
        window.location.href = 'index.html';
        return;
    }

    // Load playlists in sidebar
    const playlistsContainer = document.getElementById('userPlaylists');
    if (playlistsContainer) {
        playlistsContainer.innerHTML = '';

        currentUser.playlists.forEach(playlist => {
            const li = document.createElement('li');
            li.className = 'nav-item';

            const a = document.createElement('a');
            a.className = 'nav-link';
            a.href = `library.html?playlist=${encodeURIComponent(playlist.name)}`;
            a.textContent = playlist.name;

            li.appendChild(a);
            playlistsContainer.appendChild(li);
        });
    }
}

// Home Page Functions
function initHomePage() {
    displayRecentlyPlayed();
    displayTrendingSongs();
}

function displayRecentlyPlayed() {
    const container = document.getElementById('recentlyPlayed');
    if (!container) return;

    container.innerHTML = '';

    // Get last 5 played songs (for demo, we'll use sample songs)
    const recentlyPlayed = sampleSongs.slice(0, 5);

    recentlyPlayed.forEach((song, index) => {
        const row = document.createElement('tr');
        row.className = 'song-row';
        row.dataset.id = song.id;

        row.innerHTML = `
            <th scope="row">${index + 1}</th>
            <td>
                <div class="d-flex align-items-center">
                    <img src="${song.coverUrl}" alt="${song.album}" class="me-3" width="40">
                    <div>
                        <div>${song.title}</div>
                        <div class="text-muted small">${song.artist}</div>
                    </div>
                </div>
            </td>
            <td>${song.artist}</td>
            <td>${song.album}</td>
            <td>${song.duration}</td>
            <td>
                <button class="btn btn-sm btn-outline-primary me-2 play-btn">Play</button>
                <button class="btn btn-sm btn-outline-secondary like-btn">Like</button>
            </td>
        `;

        container.appendChild(row);
    });
}

function displayTrendingSongs() {
    const container = document.querySelector('.trending-now .row');
    if (!container) return;

    container.innerHTML = '';

    // Get trending songs (for demo, we'll use sample songs)
    const trendingSongs = [...sampleSongs].sort(() => 0.5 - Math.random()).slice(0, 4);

    trendingSongs.forEach(song => {
        const col = document.createElement('div');
        col.className = 'col-md-3 mb-4';

        col.innerHTML = `
            <div class="card song-card">
                <img src="${song.coverUrl}" class="card-img-top" alt="${song.album}">
                <div class="card-body">
                    <h5 class="card-title">${song.title}</h5>
                    <p class="card-text text-muted">${song.artist}</p>
                    <button class="btn btn-sm btn-primary play-btn">Play</button>
                </div>
            </div>
        `;

        container.appendChild(col);
    });
}

// Search Page Functions
function initSearchPage() {
    const searchBtn = document.getElementById('searchBtn');
    const searchInput = document.getElementById('searchInput');

    searchBtn.addEventListener('click', function () {
        performSearch(searchInput.value);
    });

    searchInput.addEventListener('keypress', function (e) {
        if (e.key === 'Enter') {
            performSearch(this.value);
        }
    });
}

function performSearch(query) {
    const resultsContainer = document.getElementById('searchResults');
    if (!resultsContainer) return;

    resultsContainer.innerHTML = '';

    if (!query || query.trim() === '') {
        // Show all songs if empty search
        displayAllSongs(resultsContainer);
        return;
    }

    // Filter songs based on query
    const filteredSongs = sampleSongs.filter(song =>
        song.title.toLowerCase().includes(query.toLowerCase()) ||
        song.artist.toLowerCase().includes(query.toLowerCase()) ||
        song.album.toLowerCase().includes(query.toLowerCase())
    );

    if (filteredSongs.length === 0) {
        resultsContainer.innerHTML = '<tr><td colspan="6" class="text-center py-4">No results found</td></tr>';
        return;
    }

    displaySongsInTable(filteredSongs, resultsContainer);
}

function displayAllSongs(container) {
    displaySongsInTable(sampleSongs, container);
}

function displaySongsInTable(songs, container) {
    container.innerHTML = '';

    songs.forEach((song, index) => {
        const row = document.createElement('tr');
        row.className = 'song-row';
        row.dataset.id = song.id;

        row.innerHTML = `
            <th scope="row">${index + 1}</th>
            <td>
                <div class="d-flex align-items-center">
                    <img src="${song.coverUrl}" alt="${song.album}" class="me-3" width="40">
                    <div>
                        <div>${song.title}</div>
                        <div class="text-muted small">${song.artist}</div>
                    </div>
                </div>
            </td>
            <td>${song.artist}</td>
            <td>${song.album}</td>
            <td>${song.duration}</td>
            <td>
                <button class="btn btn-sm btn-outline-primary me-2 play-btn">Play</button>
                <button class="btn btn-sm btn-outline-secondary like-btn">Like</button>
                <button class="btn btn-sm btn-outline-success download-btn">Download</button>
            </td>
        `;

        container.appendChild(row);
    });
}

// Library Page Functions
function initLibraryPage() {
    const urlParams = new URLSearchParams(window.location.search);
    const filter = urlParams.get('filter');
    const playlist = urlParams.get('playlist');

    if (filter === 'liked') {
        displayLikedSongs();
    } else if (filter === 'downloaded') {
        displayDownloadedSongs();
    } else if (playlist) {
        displayPlaylistSongs(playlist);
    } else {
        // Default view (all library songs)
        displayLibrarySongs();
    }
}

function displayLikedSongs() {
    const currentUser = JSON.parse(localStorage.getItem('currentUser'));
    const likedSongs = sampleSongs.filter(song =>
        currentUser.likedSongs.includes(song.id)
    );

    displaySongsInLibrary(likedSongs, 'Liked Songs');
}

function displayDownloadedSongs() {
    const currentUser = JSON.parse(localStorage.getItem('currentUser'));
    const downloadedSongs = sampleSongs.filter(song =>
        currentUser.downloadedSongs.includes(song.id)
    );

    displaySongsInLibrary(downloadedSongs, 'Downloaded Songs');
}

function displayPlaylistSongs(playlistName) {
    const currentUser = JSON.parse(localStorage.getItem('currentUser'));
    const playlist = currentUser.playlists.find(p => p.name === playlistName);

    if (!playlist) {
        displaySongsInLibrary([], playlistName);
        return;
    }

    const playlistSongs = sampleSongs.filter(song =>
        playlist.songs.includes(song.id)
    );

    displaySongsInLibrary(playlistSongs, playlistName);
}

function displayLibrarySongs() {
    const currentUser = JSON.parse(localStorage.getItem('currentUser'));
    const allSongs = [...sampleSongs];

    displaySongsInLibrary(allSongs, 'My Library');
}

function displaySongsInLibrary(songs, title) {
    const container = document.getElementById('librarySongs');
    const titleEl = document.getElementById('libraryTitle');

    if (!container || !titleEl) return;

    titleEl.textContent = title;
    container.innerHTML = '';

    if (songs.length === 0) {
        container.innerHTML = '<tr><td colspan="6" class="text-center py-4">No songs found</td></tr>';
        return;
    }

    displaySongsInTable(songs, container);
}

// Playlist Page Functions
function initPlaylistPage() {
    const createBtn = document.getElementById('createPlaylistBtn');
    const saveBtn = document.getElementById('savePlaylistBtn');

    createBtn.addEventListener('click', function () {
        document.getElementById('playlistCreator').style.display = 'block';
    });

    saveBtn.addEventListener('click', function () {
        const name = document.getElementById('playlistName').value.trim();
        const description = document.getElementById('playlistDescription').value.trim();

        if (!name) {
            alert('Please enter a playlist name');
            return;
        }

        const currentUser = JSON.parse(localStorage.getItem('currentUser'));

        // Check if playlist already exists
        if (currentUser.playlists.some(p => p.name === name)) {
            alert('A playlist with this name already exists');
            return;
        }

        // Add new playlist
        const newPlaylist = {
            name,
            description,
            songs: [] // Will be added when songs are selected
        };

        currentUser.playlists.push(newPlaylist);
        localStorage.setItem('currentUser', JSON.stringify(currentUser));

        // Reload playlists in sidebar
        loadUserData();

        // Reset form
        document.getElementById('playlistCreator').style.display = 'none';
        document.getElementById('playlistName').value = '';
        document.getElementById('playlistDescription').value = '';
    });

    // Display available songs
    displayAvailableSongs();
}

function displayAvailableSongs() {
    const container = document.getElementById('availableSongs');
    if (!container) return;

    container.innerHTML = '';

    sampleSongs.forEach((song, index) => {
        const row = document.createElement('tr');
        row.className = 'song-row';
        row.dataset.id = song.id;

        row.innerHTML = `
            <th scope="row">${index + 1}</th>
            <td>
                <div class="d-flex align-items-center">
                    <img src="${song.coverUrl}" alt="${song.album}" class="me-3" width="40">
                    <div>
                        <div>${song.title}</div>
                        <div class="text-muted small">${song.artist}</div>
                    </div>
                </div>
            </td>
            <td>${song.artist}</td>
            <td>${song.album}</td>
            <td>${song.duration}</td>
            <td>
                <select class="form-select form-select-sm add-to-playlist">
                    <option value="">Select playlist</option>
                    <!-- Playlist options will be added dynamically -->
                </select>
            </td>
        `;

        container.appendChild(row);

        // Add playlist options
        const select = row.querySelector('.add-to-playlist');
        const currentUser = JSON.parse(localStorage.getItem('currentUser'));

        currentUser.playlists.forEach(playlist => {
            const option = document.createElement('option');
            option.value = playlist.name;
            option.textContent = playlist.name;
            select.appendChild(option);
        });

        // Handle playlist selection
        select.addEventListener('change', function () {
            if (!this.value) return;

            const playlistName = this.value;
            const playlist = currentUser.playlists.find(p => p.name === playlistName);

            if (!playlist.songs.includes(song.id)) {
                playlist.songs.push(song.id);
                localStorage.setItem('currentUser', JSON.stringify(currentUser));
                alert(`"${song.title}" added to "${playlistName}"`);
            } else {
                alert(`"${song.title}" is already in "${playlistName}"`);
            }

            // Reset selection
            this.value = '';
        });
    });
}