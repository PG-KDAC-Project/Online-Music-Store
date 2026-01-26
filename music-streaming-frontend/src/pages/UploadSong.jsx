import { useState } from 'react';
import { songService } from '../services/songService';
import '../styles/upload.css';

export default function UploadSong() {
  const [file, setFile] = useState(null);
  const [coverImage, setCoverImage] = useState(null);
  const [metadata, setMetadata] = useState({
    title: '',
    album: '',
    genre: '',
    language: '',
    duration: 0
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const handleFileChange = (e) => {
    const selectedFile = e.target.files[0];
    if (selectedFile && selectedFile.type === 'audio/mpeg') {
      setFile(selectedFile);
      setError('');
      
      const audio = new Audio();
      audio.src = URL.createObjectURL(selectedFile);
      audio.onloadedmetadata = () => {
        setMetadata(prev => ({ ...prev, duration: Math.floor(audio.duration) }));
      };
    } else {
      setError('Please select an MP3 file');
      setFile(null);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!file) {
      setError('Please select a file');
      return;
    }

    setLoading(true);
    setError('');
    setSuccess('');

    try {
      const formData = new FormData();
      formData.append('file', file);
      if (coverImage) {
        formData.append('coverImage', coverImage);
      }
      formData.append('title', metadata.title);
      formData.append('album', metadata.album);
      formData.append('genre', metadata.genre);
      formData.append('language', metadata.language);
      formData.append('duration', metadata.duration);

      await songService.uploadSong(formData);
      setSuccess('Song uploaded successfully!');
      setFile(null);
      setCoverImage(null);
      setMetadata({ title: '', album: '', genre: '', language: '', duration: 0 });
    } catch (err) {
      setError(err.response?.data?.message || 'Upload failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="upload-container">
      <h1>Upload Song</h1>
      <p className="subtitle">Share your music with the world</p>

      {error && <div className="alert alert-error">{error}</div>}
      {success && <div className="alert alert-success">{success}</div>}

      <form onSubmit={handleSubmit} className="upload-form">
        <div className="form-group">
          <label>Audio File (MP3)</label>
          <input type="file" accept="audio/mpeg" onChange={handleFileChange} required />
          {file && <small>{file.name}</small>}
        </div>

        <div className="form-group">
          <label>Cover Image (Optional)</label>
          <input type="file" accept="image/*" onChange={(e) => setCoverImage(e.target.files[0])} />
          {coverImage && <small>{coverImage.name}</small>}
        </div>

        <div className="form-group">
          <label>Title</label>
          <input
            type="text"
            value={metadata.title}
            onChange={(e) => setMetadata({ ...metadata, title: e.target.value })}
            required
          />
        </div>

        <div className="form-group">
          <label>Album</label>
          <input
            type="text"
            value={metadata.album}
            onChange={(e) => setMetadata({ ...metadata, album: e.target.value })}
          />
        </div>

        <div className="form-group">
          <label>Genre</label>
          <select
            value={metadata.genre}
            onChange={(e) => setMetadata({ ...metadata, genre: e.target.value })}
            required
          >
            <option value="">Select Genre</option>
            <option value="Pop">Pop</option>
            <option value="Rock">Rock</option>
            <option value="Hip-Hop">Hip-Hop</option>
            <option value="Electronic">Electronic</option>
            <option value="Jazz">Jazz</option>
            <option value="Classical">Classical</option>
          </select>
        </div>

        <div className="form-group">
          <label>Language</label>
          <input
            type="text"
            value={metadata.language}
            onChange={(e) => setMetadata({ ...metadata, language: e.target.value })}
            required
          />
        </div>

        <button type="submit" className="btn-primary" disabled={loading}>
          {loading ? 'Uploading...' : 'Upload Song'}
        </button>
      </form>
    </div>
  );
}
