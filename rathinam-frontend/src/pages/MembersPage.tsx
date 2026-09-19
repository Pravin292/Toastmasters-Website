import React, { useEffect, useState, useMemo } from 'react';
import { AppLayout } from '../components/layout/AppLayout';
import { memberApi } from '../api/memberApi';
import { Member } from '../types';
import { useAuth } from '../context/AuthContext';
import { 
  Users, 
  GraduationCap, 
  TrendingUp, 
  Trophy, 
  Search, 
  Plus, 
  MoreHorizontal, 
  ChevronLeft, 
  ChevronRight,
  X,
  Sparkles
} from 'lucide-react';
import '../styles/MembersPage.css';

interface DisplayMember {
  id: string;
  name: string;
  role: string;
  pathway: string;
  speeches: number;
  attendance: string;
  status: 'Active' | 'Inactive' | 'Alumni';
  category: 'All Members' | 'Executive Committee' | 'Mentors' | 'Alumni';
  avatarUrl?: string;
}




export const MembersPage: React.FC = () => {
  const { isOfficer } = useAuth();

  const [activeTab, setActiveTab] = useState<'All Members' | 'Executive Committee' | 'Mentors' | 'Alumni'>('All Members');
  const [searchQuery, setSearchQuery] = useState('');
  const [roleFilter, setRoleFilter] = useState('All');
  const [pathwayFilter, setPathwayFilter] = useState('All');
  const [statusFilter, setStatusFilter] = useState('All');
  const [currentPage, setCurrentPage] = useState(1);
  const pageSize = 5;

  // Modal State
  const [isAddModalOpen, setIsAddModalOpen] = useState(false);
  const [newFirstName, setNewFirstName] = useState('');
  const [newLastName, setNewLastName] = useState('');
  const [newEmail, setNewEmail] = useState('');
  const [newPathway, setNewPathway] = useState('Dynamic Leadership');
  const [creating, setCreating] = useState(false);

  // Backend members list
  const [backendMembers, setBackendMembers] = useState<DisplayMember[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchLiveMembers = async () => {
      setLoading(true);
      try {
        const res = await memberApi.getMembers();
        if (res && res.length > 0) {
          const mapped: DisplayMember[] = res.map((m: Member) => {
            const role: string = m.email?.toLowerCase().includes('admin')
              ? 'President'
              : (m.email?.toLowerCase().includes('officer') ? 'VP Education' : 'Member');
            const isExComm = role === 'President' || role.includes('VP') || role === 'Secretary' || role === 'Treasurer' || role === 'Sergeant At Arms';
            const statusVal: DisplayMember['status'] = m.status?.toUpperCase() === 'ALUMNI'
              ? 'Alumni'
              : (m.status?.toUpperCase() === 'INACTIVE' ? 'Inactive' : 'Active');
            const category: DisplayMember['category'] = statusVal === 'Alumni'
              ? 'Alumni'
              : (isExComm ? 'Executive Committee' : 'All Members');

            return {
              id: m.id,
              name: m.displayName || `${m.firstName || ''} ${m.lastName || ''}`.trim() || m.email,
              role,
              pathway: 'Presentation Mastery',
              speeches: 0,
              attendance: '100%',
              status: statusVal,
              category,
              avatarUrl: m.profilePictureUrl || undefined,
            };
          });
          setBackendMembers(mapped);
        } else {
          setBackendMembers([]);
        }
      } catch (err) {
        console.error('Failed to fetch members:', err);
        setBackendMembers([]);
      } finally {
        setLoading(false);
      }
    };
    fetchLiveMembers();
  }, []);

  // Real backend members list only (dummy data removed)
  const allMembersList = useMemo(() => {
    return backendMembers;
  }, [backendMembers]);

  // Tab Filtering + Search + Dropdown Filters
  const filteredMembers = useMemo(() => {
    return allMembersList.filter((m) => {
      // Tab check
      if (activeTab === 'Executive Committee' && m.category !== 'Executive Committee' && !m.role.includes('VP') && m.role !== 'President' && m.role !== 'Secretary' && m.role !== 'Treasurer' && m.role !== 'Sergeant At Arms') {
        return false;
      }
      if (activeTab === 'Mentors' && m.category !== 'Mentors' && m.role !== 'Mentor') {
        return false;
      }
      if (activeTab === 'Alumni' && m.category !== 'Alumni' && m.status !== 'Alumni') {
        return false;
      }

      // Search Query
      if (searchQuery.trim()) {
        const q = searchQuery.toLowerCase();
        const match = m.name.toLowerCase().includes(q) || 
                      m.role.toLowerCase().includes(q) || 
                      m.pathway.toLowerCase().includes(q);
        if (!match) return false;
      }

      // Dropdowns
      if (roleFilter !== 'All' && m.role !== roleFilter) return false;
      if (pathwayFilter !== 'All' && m.pathway !== pathwayFilter) return false;
      if (statusFilter !== 'All' && m.status !== statusFilter) return false;

      return true;
    });
  }, [allMembersList, activeTab, searchQuery, roleFilter, pathwayFilter, statusFilter]);

  // Pagination calculation
  const totalItems = filteredMembers.length;
  const totalPages = Math.max(1, Math.ceil(totalItems / pageSize));
  const displayedMembers = filteredMembers.slice((currentPage - 1) * pageSize, currentPage * pageSize);

  const handleCreateMember = async (e: React.FormEvent) => {
    e.preventDefault();
    setCreating(true);
    try {
      await memberApi.createMember({
        firstName: newFirstName,
        lastName: newLastName,
        email: newEmail,
        joinDate: new Date().toISOString()
      });

      // Append locally
      const newMemberItem: DisplayMember = {
        id: `custom-${Date.now()}`,
        name: `${newFirstName} ${newLastName}`,
        role: 'Member',
        pathway: newPathway,
        speeches: 0,
        attendance: '100%',
        status: 'Active',
        category: 'All Members',
      };
      setBackendMembers((prev) => [newMemberItem, ...prev]);
      setIsAddModalOpen(false);
      setNewFirstName('');
      setNewLastName('');
      setNewEmail('');
    } catch (err) {
      console.error(err);
    } finally {
      setCreating(false);
    }
  };

  return (
    <AppLayout fluid={true}>
      <div className="members-page-wrapper">
        
        {/* ==================================================================
            1. FULL-BLEED PANORAMIC HERO BANNER (BLENDED EXCOMM BACKGROUND)
            ================================================================== */}
        {/* ==================================================================
            1. FULL-BLEED PANORAMIC HERO BANNER (BLENDED EXCOMM BACKGROUND & REAL HTML TEXT)
            ================================================================== */}
        <section className="members-hero-banner" aria-label="Rathinam Toastmasters Club ExComm Banner">
          <div className="members-hero-inner">
            
            {/* Left Column: Heading, Subtitle, and Tag */}
            <div className="members-hero-left">
              <span className="members-hero-tag">MEMBERS</span>
              <h1 className="members-hero-title">
                People Make<br />Great Clubs
              </h1>
              <p className="members-hero-subtitle">
                Meet our members — a community of learners, leaders and changemakers.
              </p>
              <div className="members-hero-accent-bar" />
            </div>

            {/* Center Column: Rathinam Club Metadata & The 7 Persons Image Cutout */}
            <div className="members-hero-center">
              <div className="members-hero-club-meta-block">
                <h2 className="members-hero-club-name">RATHINAM TOASTMASTERS CLUB</h2>
                <div className="members-hero-club-divider">
                  <span className="gold-line" />
                  <span className="gold-diamond">◆</span>
                  <span className="gold-line" />
                </div>
                <div className="members-hero-club-tags">
                  <span>CLUB NO: 28679922</span>
                  <span className="sep">•</span>
                  <span>AREA B3</span>
                  <span className="sep">•</span>
                  <span>DIVISION B</span>
                  <span className="sep">•</span>
                  <span>DISTRICT 230</span>
                </div>
              </div>

              {/* ExComm Floating Title (Left of Persons) */}
              <div className="members-hero-excomm-title-block">
                <div className="excomm-badge-row">
                  <span className="excomm-prefix">MEET THE</span>
                  <span className="excomm-main">EXCOMM</span>
                </div>
                <span className="excomm-term">JULY - DECEMBER</span>
              </div>

              {/* The 7 Isolated Persons Cutout resting on the bottom baseline */}
              <div className="members-hero-persons-stage">
                <img 
                  src="/assets/excomm-persons-clean.png" 
                  alt="Rathinam Toastmasters Executive Committee - Pravin Lenin Naidu, Dharsini, Swaathy Sahaana, Benita Biju, Ashleenin Shamma, Boomika, Kalvin Paul" 
                  className="members-hero-persons-cutout"
                />
              </div>
            </div>

            {/* Right Column: Inspirational Toastmasters Quote */}
            <div className="members-hero-right">
              <div className="members-hero-quote-card">
                <p className="quote-main-text">“Better Speakers<br />A Better World”</p>
                <div className="quote-author-line">
                  <span className="author-dash">—</span> Toastmasters International
                </div>
              </div>
            </div>

          </div>
        </section>

        {/* ==================================================================
            2. INSPIRATIONAL QUOTE STRIP
            ================================================================== */}
        <div className="members-quote-strip">
          <div className="members-quote-strip-inner">
            <Sparkles size={16} color="#77216F" />
            <span>
              “We learn best in moments of enjoyment.” — <strong>Dr. Ralph C. Smedley</strong>, Founder of Toastmasters International
            </span>
          </div>
        </div>

        {/* ==================================================================
            3. CATEGORY SUB-TABS (ALL MEMBERS, EXCOMM, MENTORS, ALUMNI)
            ================================================================== */}
        <div className="members-tabs-bar">
          {(['All Members', 'Executive Committee', 'Mentors', 'Alumni'] as const).map((tab) => (
            <button
              key={tab}
              type="button"
              className={`members-tab-btn ${activeTab === tab ? 'active' : ''}`}
              onClick={() => {
                setActiveTab(tab);
                setCurrentPage(1);
              }}
            >
              {tab}
            </button>
          ))}
        </div>

        {/* Main Content Area */}
        <div className="members-content-container">

          {/* ================================================================
              4. 4 METRIC KPI STAT CARDS
              ================================================================ */}
          <div className="members-kpi-grid">
            {/* KPI 1 */}
            <div className="members-kpi-card">
              <div className="members-kpi-icon-box" style={{ background: '#FDF2F8' }}>
                <Users size={24} color="#BE185D" />
              </div>
              <div className="members-kpi-info">
                <span className="members-kpi-number">{allMembersList.length}</span>
                <span className="members-kpi-label">Total Members</span>
                <span className="members-kpi-trend">Registered members</span>
              </div>
            </div>

            {/* KPI 2 */}
            <div className="members-kpi-card">
              <div className="members-kpi-icon-box" style={{ background: '#F5F3FF' }}>
                <GraduationCap size={24} color="#7C3AED" />
              </div>
              <div className="members-kpi-info">
                <span className="members-kpi-number">
                  {allMembersList.filter((m) => m.category === 'Executive Committee' || m.role.includes('VP') || m.role === 'President').length}
                </span>
                <span className="members-kpi-label">Executive Committee</span>
                <span className="members-kpi-trend">Club officers</span>
              </div>
            </div>

            {/* KPI 3 */}
            <div className="members-kpi-card">
              <div className="members-kpi-icon-box" style={{ background: '#ECFDF5' }}>
                <TrendingUp size={24} color="#059669" />
              </div>
              <div className="members-kpi-info">
                <span className="members-kpi-number">
                  {allMembersList.filter((m) => m.status === 'Active').length}
                </span>
                <span className="members-kpi-label">Active Members</span>
                <span className="members-kpi-trend">In good standing</span>
              </div>
            </div>

            {/* KPI 4 */}
            <div className="members-kpi-card">
              <div className="members-kpi-icon-box" style={{ background: '#FFFBEB' }}>
                <Trophy size={24} color="#D97706" />
              </div>
              <div className="members-kpi-info">
                <span className="members-kpi-number">
                  {allMembersList.filter((m) => m.status === 'Alumni' || m.category === 'Alumni').length}
                </span>
                <span className="members-kpi-label">Alumni</span>
                <span className="members-kpi-trend">Past members</span>
              </div>
            </div>
          </div>

          {/* ================================================================
              5. SEARCH AND FILTER ACTION BAR
              ================================================================ */}
          <div className="members-filter-bar">
            {/* Search Field */}
            <div className="members-search-box">
              <Search size={18} className="members-search-icon" />
              <input
                type="text"
                placeholder="Search members by name, role, or pathway..."
                value={searchQuery}
                onChange={(e) => {
                  setSearchQuery(e.target.value);
                  setCurrentPage(1);
                }}
                className="members-search-input"
              />
            </div>

            {/* Filter Dropdowns & Add Button */}
            <div className="members-dropdown-group">
              <select
                value={roleFilter}
                onChange={(e) => {
                  setRoleFilter(e.target.value);
                  setCurrentPage(1);
                }}
                className="members-select"
              >
                <option value="All">All Roles</option>
                <option value="Member">Member</option>
                <option value="VP Education">VP Education</option>
                <option value="President">President</option>
                <option value="VP PR">VP PR</option>
                <option value="VP Membership">VP Membership</option>
                <option value="Secretary">Secretary</option>
                <option value="Treasurer">Treasurer</option>
                <option value="Sergeant At Arms">Sergeant At Arms</option>
                <option value="Mentor">Mentor</option>
              </select>

              <select
                value={pathwayFilter}
                onChange={(e) => {
                  setPathwayFilter(e.target.value);
                  setCurrentPage(1);
                }}
                className="members-select"
              >
                <option value="All">All Pathways</option>
                <option value="Dynamic Leadership">Dynamic Leadership</option>
                <option value="Engaging Relational">Engaging Relational</option>
                <option value="Presentation Mastery">Presentation Mastery</option>
                <option value="Motivational Strategies">Motivational Strategies</option>
                <option value="Persuasive Influence">Persuasive Influence</option>
                <option value="Visionary Communication">Visionary Communication</option>
              </select>

              <select
                value={statusFilter}
                onChange={(e) => {
                  setStatusFilter(e.target.value);
                  setCurrentPage(1);
                }}
                className="members-select"
              >
                <option value="All">All Status</option>
                <option value="Active">Active</option>
                <option value="Inactive">Inactive</option>
                <option value="Alumni">Alumni</option>
              </select>

              {/* Add Member Button (Toastmasters Maroon) */}
              <button
                type="button"
                className="members-add-btn"
                onClick={() => setIsAddModalOpen(true)}
              >
                <Plus size={18} />
                <span>Add Member</span>
              </button>
            </div>
          </div>

          {/* ================================================================
              6. MEMBERS DATA TABLE
              ================================================================ */}
          <div className="members-table-card">
            <div className="members-table-container">
              <table className="members-table">
                <thead>
                  <tr>
                    <th style={{ width: '40px' }}>#</th>
                    <th>Member</th>
                    <th>Role</th>
                    <th>Pathway</th>
                    <th style={{ textAlign: 'center' }}>Speeches</th>
                    <th style={{ textAlign: 'center' }}>Attendance</th>
                    <th>Status</th>
                    <th style={{ width: '50px', textAlign: 'center' }}>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {loading ? (
                    <tr>
                      <td colSpan={8} style={{ textAlign: 'center', padding: '40px 20px', color: '#64748B' }}>
                        Loading members...
                      </td>
                    </tr>
                  ) : displayedMembers.length === 0 ? (
                    <tr>
                      <td colSpan={8} style={{ textAlign: 'center', padding: '40px 20px', color: '#64748B' }}>
                        No members found matching your search and filter criteria.
                      </td>
                    </tr>
                  ) : (
                    displayedMembers.map((m, index) => {
                      const rowIndex = (currentPage - 1) * pageSize + index + 1;
                      const isOfficerRole = m.role.includes('VP') || m.role === 'President' || m.role === 'Secretary' || m.role === 'Treasurer' || m.role === 'Sergeant At Arms';
                      const roleClass = m.role === 'President' 
                        ? 'members-role-president' 
                        : (isOfficerRole ? 'members-role-officer' : 'members-role-member');

                      return (
                        <tr key={m.id}>
                          {/* Row Index */}
                          <td className="members-col-index">{rowIndex}</td>

                          {/* Member Avatar + Name */}
                          <td>
                            <div className="members-user-cell">
                              {m.avatarUrl ? (
                                <img
                                  src={m.avatarUrl}
                                  alt={m.name}
                                  className="members-table-avatar"
                                  onError={(e) => {
                                    // Fallback to initial
                                    (e.currentTarget as HTMLElement).style.display = 'none';
                                  }}
                                />
                              ) : (
                                <div className="members-table-avatar">
                                  {m.name.charAt(0)}
                                </div>
                              )}
                              <span className="members-name-text">{m.name}</span>
                            </div>
                          </td>

                          {/* Role Pill */}
                          <td>
                            <span className={`members-role-pill ${roleClass}`}>
                              {m.role}
                            </span>
                          </td>

                          {/* Pathway */}
                          <td className="members-pathway-text">{m.pathway}</td>

                          {/* Speeches Completed */}
                          <td style={{ textAlign: 'center', fontWeight: 600 }}>{m.speeches}</td>

                          {/* Attendance Rate */}
                          <td style={{ textAlign: 'center', fontWeight: 600 }}>{m.attendance}</td>

                          {/* Status */}
                          <td>
                            <span className="members-status-pill">
                              <span className="members-status-dot" />
                              {m.status}
                            </span>
                          </td>

                          {/* Actions */}
                          <td style={{ textAlign: 'center' }}>
                            <button
                              type="button"
                              className="members-action-btn"
                              title="Member Options"
                            >
                              <MoreHorizontal size={18} />
                            </button>
                          </td>
                        </tr>
                      );
                    })
                  )}
                </tbody>
              </table>
            </div>

            {/* Pagination Footer */}
            <div className="members-pagination-bar">
              <div className="members-pagination-info">
                Showing {totalItems === 0 ? 0 : (currentPage - 1) * pageSize + 1}–
                {Math.min(currentPage * pageSize, totalItems)} of {totalItems} members
              </div>

              <div className="members-pagination-nav">
                <button
                  type="button"
                  className="members-page-btn"
                  disabled={currentPage === 1}
                  onClick={() => setCurrentPage((p) => Math.max(1, p - 1))}
                  title="Previous Page"
                >
                  <ChevronLeft size={16} />
                </button>

                {Array.from({ length: totalPages }, (_, i) => i + 1).map((pageNum) => (
                  <button
                    key={pageNum}
                    type="button"
                    className={`members-page-btn ${currentPage === pageNum ? 'active' : ''}`}
                    onClick={() => setCurrentPage(pageNum)}
                  >
                    {pageNum}
                  </button>
                ))}

                <button
                  type="button"
                  className="members-page-btn"
                  disabled={currentPage === totalPages}
                  onClick={() => setCurrentPage((p) => Math.min(totalPages, p + 1))}
                  title="Next Page"
                >
                  <ChevronRight size={16} />
                </button>
              </div>
            </div>
          </div>

        </div>

        {/* ==================================================================
            7. ADD MEMBER MODAL
            ================================================================== */}
        {isAddModalOpen && (
          <div className="members-modal-backdrop" onClick={() => setIsAddModalOpen(false)}>
            <div className="members-modal-box" onClick={(e) => e.stopPropagation()}>
              <div className="members-modal-header">
                <h3 className="members-modal-title">Register New Club Member</h3>
                <button
                  type="button"
                  onClick={() => setIsAddModalOpen(false)}
                  style={{ background: 'none', border: 'none', color: '#64748B', cursor: 'pointer' }}
                >
                  <X size={20} />
                </button>
              </div>

              <form onSubmit={handleCreateMember}>
                <div className="members-modal-body">
                  <div className="members-form-group">
                    <label className="members-form-label">First Name</label>
                    <input
                      type="text"
                      required
                      placeholder="e.g. Arjun"
                      value={newFirstName}
                      onChange={(e) => setNewFirstName(e.target.value)}
                      className="members-form-input"
                    />
                  </div>

                  <div className="members-form-group">
                    <label className="members-form-label">Last Name</label>
                    <input
                      type="text"
                      required
                      placeholder="e.g. Sharma"
                      value={newLastName}
                      onChange={(e) => setNewLastName(e.target.value)}
                      className="members-form-input"
                    />
                  </div>

                  <div className="members-form-group">
                    <label className="members-form-label">Email Address</label>
                    <input
                      type="email"
                      required
                      placeholder="member@rathinam.com"
                      value={newEmail}
                      onChange={(e) => setNewEmail(e.target.value)}
                      className="members-form-input"
                    />
                  </div>

                  <div className="members-form-group">
                    <label className="members-form-label">Pathway Track</label>
                    <select
                      value={newPathway}
                      onChange={(e) => setNewPathway(e.target.value)}
                      className="members-form-input"
                    >
                      <option value="Dynamic Leadership">Dynamic Leadership</option>
                      <option value="Engaging Relational">Engaging Relational</option>
                      <option value="Presentation Mastery">Presentation Mastery</option>
                      <option value="Motivational Strategies">Motivational Strategies</option>
                      <option value="Persuasive Influence">Persuasive Influence</option>
                      <option value="Visionary Communication">Visionary Communication</option>
                    </select>
                  </div>
                </div>

                <div className="members-modal-footer">
                  <button
                    type="button"
                    className="members-cancel-btn"
                    onClick={() => setIsAddModalOpen(false)}
                  >
                    Cancel
                  </button>
                  <button
                    type="submit"
                    disabled={creating}
                    className="members-submit-btn"
                  >
                    {creating ? 'Registering...' : 'Add Member'}
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}

      </div>
    </AppLayout>
  );
};
