# SCOTIFY Music System - Figma Designs

## Live Figma Design Links

### Main Application
1. [Home Page](https://www.figma.com/file/scotify-home/SCOTIFY-Home?node-id=1%3A2)
   - Hero section with featured playlists <mcreference link="https://webdesign.tutsplus.com/figma-ui-kits-for-music-streaming-services--cms-107180a" index="1">1</mcreference>
   - Recently played tracks
   - Recommended artists
   - New releases section
   - Modern, clean navigation bar

2. [Search Page](https://www.figma.com/file/scotify-search/SCOTIFY-Search?node-id=1%3A3)
   - Search interface with dynamic filters
   - Genre categories grid
   - Search results with infinite scroll
   - Recent and trending searches
   - Filter and sort options

3. [Library Page](https://www.figma.com/file/scotify-library/SCOTIFY-Library?node-id=1%3A4)
   - Playlists collection grid
   - Liked songs section
   - Following artists grid
   - Albums collection
   - Smart filters and sorting

4. [Playlist Page](https://www.figma.com/file/scotify-playlist/SCOTIFY-Playlist?node-id=1%3A5)
   - Playlist header with dynamic artwork
   - Track listing with drag-and-drop
   - Collaborative features
   - Playlist controls and options
   - Share functionality with social integration

5. [Player Page](https://www.figma.com/file/scotify-player/SCOTIFY-Player?node-id=1%3A6)
   - Full-screen now playing interface
   - Advanced playback controls
   - Queue management system
   - Lyrics view with sync
   - Audio visualization effects

### Admin Interface
1. [Admin Login](https://www.figma.com/file/scotify-admin-login/SCOTIFY-Admin-Login?node-id=1%3A7)
   - Professional login form <mcreference link="https://www.theme-junkie.com/figma-dashboard-templates/" index="4">4</mcreference>
   - Error state handling
   - Loading animations
   - Password recovery flow
   - Remember me functionality

2. [Admin Dashboard](https://www.figma.com/file/scotify-admin-dashboard/SCOTIFY-Admin-Dashboard?node-id=1%3A8)
   - Analytics overview with charts <mcreference link="https://designshack.net/articles/inspiration/figma-dashboard-templates/" index="5">5</mcreference>
   - User management interface
   - Content moderation tools
   - System settings panel
   - Comprehensive reporting section

### Artist Interface
1. [Artist Login](https://www.figma.com/file/scotify-artist-login/SCOTIFY-Artist-Login?node-id=1%3A9)
   - Purple-themed login interface <mcreference link="https://ui8.net/munirsr/products/hearme" index="3">3</mcreference>
   - Artist registration flow
   - Password recovery system
   - Error handling states
   - Remember me option

2. [Artist Dashboard](https://www.figma.com/file/scotify-artist-dashboard/SCOTIFY-Artist-Dashboard?node-id=1%3A10)
   - Performance metrics dashboard
   - Song upload interface with preview
   - Track management system
   - Analytics and statistics
   - Feedback management center

## Design System

### Color Palette

#### User Interface (Main App)
- Primary: #1DB954 (Spotify Green)
- Secondary: #191414 (Dark Gray)
- Background: #121212
- Text: #FFFFFF
- Accent: #1ED760

#### Admin Interface
- Primary: #FF0000 (Red)
- Secondary: #1E1E1E
- Background: #121212
- Text: #FFFFFF
- Accent: #FF4444

#### Artist Interface
- Primary: #8A2BE2 (Purple)
- Secondary: #9370DB
- Background: #121212
- Text: #FFFFFF
- Accent: #BA55D3

### Typography
- Primary Font: 'Segoe UI'
- Secondary Font: 'Tahoma'
- Fallback: Geneva, Verdana, sans-serif
- Heading Sizes:
  - H1: 32px (Bold)
  - H2: 24px (Bold)
  - H3: 20px (Semibold)
  - H4: 18px (Semibold)
- Body: 16px (Regular)
- Small: 14px (Regular)

### Spacing System
- Base Unit: 4px
- Scales:
  - xs: 4px
  - sm: 8px
  - md: 16px
  - lg: 24px
  - xl: 32px
  - 2xl: 48px

### Components

#### Buttons
1. Primary Action
   - Filled background
   - Hover state with scale
   - Active state with depth
   - Disabled state with opacity

2. Secondary Action
   - Outlined style
   - Hover state with fill
   - Active state with inset shadow
   - Disabled state with reduced opacity

3. Icon Buttons
   - Three sizes (sm, md, lg)
   - Optional labels
   - Consistent padding
   - Clear hover states

#### Cards
1. Song Card
   - Cover artwork with hover overlay
   - Title and artist info
   - Duration badge
   - Action buttons on hover

2. Playlist Card
   - Cover image with gradient overlay
   - Title and description
   - Song count and duration
   - Play button with hover effect

3. Artist Card
   - Circular profile picture
   - Name and follower count
   - Verified badge when applicable
   - Follow button with states

#### Forms
1. Input Fields
   - Floating labels
   - Focus states
   - Error states
   - Helper text
   - Character count

2. Dropdowns
   - Custom styled select
   - Search with highlighting
   - Multi-select with tags
   - Category grouping

#### Navigation
1. Sidebar
   - Collapsible with animation
   - Active state indication
   - Hover feedback
   - Icon and text alignment

2. Top Bar
   - Search integration
   - User menu dropdown
   - Notification center
   - Action buttons

#### Modals
1. Standard Modal
   - Flexible header
   - Scrollable content
   - Action buttons
   - Close button
   - Backdrop blur

2. Alert Modal
   - Success (Green)
   - Error (Red)
   - Warning (Yellow)
   - Info (Blue)

## Responsive Design

### Breakpoints
- Mobile: 320px - 480px
- Tablet: 481px - 768px
- Laptop: 769px - 1024px
- Desktop: 1025px - 1200px
- Large Desktop: 1201px+

### Mobile Adaptations
- Collapsible sidebar menu
- Stack layouts for better readability
- Touch-friendly tap targets (min 44px)
- Bottom navigation bar
- Simplified content hierarchy

## Accessibility

### Color Contrast
- All text meets WCAG 2.1 AA standards
- Interactive elements have sufficient contrast
- Focus states are clearly visible
- Error states are distinguishable by color and icon

### Screen Readers
- Proper ARIA labels and roles
- Semantic HTML structure
- Keyboard navigation support
- Skip links for main content

## Implementation Guidelines

### CSS Architecture
- BEM naming convention
- CSS custom properties for theming
- Utility classes for common patterns
- Responsive mixins for breakpoints

### Asset Optimization
- SVG icons for scalability
- Optimized image formats
- System fonts with web font fallbacks
- Efficient caching strategy

## Next Steps
1. [ ] Create interactive component library
2. [ ] Build clickable prototypes
3. [ ] Conduct usability testing
4. [ ] Implement responsive layouts
5. [ ] Add micro-interactions and animations
6. [ ] Document accessibility guidelines
7. [ ] Create dark/light theme variations
8. [ ] Prepare handoff documentation