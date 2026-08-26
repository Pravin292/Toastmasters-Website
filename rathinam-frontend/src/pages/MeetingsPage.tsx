import React, { useEffect, useState } from 'react';
import { AppLayout } from '../components/layout/AppLayout';
import { Card } from '../components/common/Card';
import { Badge } from '../components/common/Badge';
import { Button } from '../components/common/Button';
import { Modal } from '../components/common/Modal';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { ErrorMessage } from '../components/common/ErrorMessage';
import { useAuth } from '../context/AuthContext';
import { meetingApi } from '../api/meetingApi';
import { Meeting, MeetingStatus, MeetingType } from '../types';
import { Calendar, Plus, MapPin, Link as LinkIcon } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

export const MeetingsPage: React.FC = () => {
  const { isOfficer } = useAuth();
  const navigate = useNavigate();

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [meetings, setMeetings] = useState<Meeting[]>([]);
  const [filterStatus, setFilterStatus] = useState<string>('ALL');

  // Create Modal State
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [meetingNumber, setMeetingNumber] = useState<number>(101);
  const [meetingStart, setMeetingStart] = useState<string>('');
  const [theme, setTheme] = useState<string>('');
  const [meetingType, setMeetingType] = useState<MeetingType>('REGULAR');
  const [location, setLocation] = useState<string>('Rathinam College Campus');
  const [meetingUrl, setMeetingUrl] = useState<string>('');
  const [description, setDescription] = useState<string>('');
  const [creating, setCreating] = useState(false);

  const fetchMeetings = async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await meetingApi.getMeetings(0, 50);
      setMeetings(res.content);
      // Auto increment next meeting number suggestion
      if (res.content.length > 0) {
        const maxNum = Math.max(...res.content.map((m) => m.meetingNumber));
        setMeetingNumber(maxNum + 1);
      }
    } catch (err: any) {
      setError(err.message || 'Failed to fetch meetings');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchMeetings();
  }, []);

  const handleCreateMeeting = async (e: React.FormEvent) => {
    e.preventDefault();
    setCreating(true);
    setError(null);
    try {
      const formattedStart = new Date(meetingStart).toISOString();
      await meetingApi.createMeeting({
        meetingNumber,
        meetingStart: formattedStart,
        theme,
        meetingType,
        location,
        meetingUrl: meetingUrl || undefined,
        description: description || undefined,
      });
      setIsModalOpen(false);
      fetchMeetings();
    } catch (err: any) {
      setError(err.message || 'Failed to create meeting');
    } finally {
      setCreating(false);
    }
  };

  const filteredMeetings = filterStatus === 'ALL'
    ? meetings
    : meetings.filter((m) => m.status === filterStatus);

  return (
    <AppLayout title="Meetings & Roles">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 24, flexWrap: 'wrap', gap: 16 }}>
        {/* Status Filter Tabs */}
        <div style={{ display: 'flex', gap: 8, background: 'rgba(255, 255, 255, 0.05)', padding: 4, borderRadius: 10 }}>
          {['ALL', 'SCHEDULED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED'].map((st) => (
            <button
              key={st}
              onClick={() => setFilterStatus(st)}
              style={{
                padding: '6px 14px',
                borderRadius: 8,
                border: 'none',
                background: filterStatus === st ? 'var(--tm-maroon)' : 'transparent',
                color: filterStatus === st ? '#FFF' : '#94A3B8',
                fontWeight: 600,
                fontSize: '0.85rem',
                cursor: 'pointer'
              }}
            >
              {st}
            </button>
          ))}
        </div>

        {isOfficer && (
          <Button variant="gold" onClick={() => setIsModalOpen(true)}>
            <Plus size={18} /> Schedule Meeting
          </Button>
        )}
      </div>

      {error && <ErrorMessage message={error} />}

      {loading ? (
        <LoadingSpinner />
      ) : (
        <div className="card-grid" style={{ gridTemplateColumns: 'repeat(auto-fill, minmax(320px, 1fr))' }}>
          {filteredMeetings.map((m) => (
            <Card key={m.id} style={{ display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
              <div>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 12 }}>
                  <span style={{ fontSize: '1.2rem', fontWeight: 800, color: '#F2DF74' }}>
                    Meeting #{m.meetingNumber}
                  </span>
                  <Badge variant={m.status.toLowerCase() as any}>
                    {m.status}
                  </Badge>
                </div>

                <h3 style={{ fontSize: '1.15rem', marginBottom: 8 }}>
                  {m.theme || 'Regular Meeting'}
                </h3>

                <div style={{ display: 'flex', flexDirection: 'column', gap: 6, fontSize: '0.88rem', color: '#94A3B8', marginBottom: 16 }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                    <Calendar size={15} color="#A9B2B1" />
                    <span>{new Date(m.meetingStart).toLocaleString()}</span>
                  </div>
                  {m.location && (
                    <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                      <MapPin size={15} color="#A9B2B1" />
                      <span>{m.location}</span>
                    </div>
                  )}
                  {m.meetingUrl && (
                    <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                      <LinkIcon size={15} color="#A9B2B1" />
                      <a href={m.meetingUrl} target="_blank" rel="noreferrer" style={{ fontSize: '0.82rem' }}>
                        Online Join Link
                      </a>
                    </div>
                  )}
                </div>
              </div>

              <div style={{ paddingTop: 14, borderTop: '1px solid rgba(255, 255, 255, 0.08)', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <span className="badge" style={{ background: 'rgba(255,255,255,0.05)', color: '#CBD5E1' }}>
                  {m.meetingType}
                </span>
                <Button variant="primary" style={{ padding: '6px 14px', fontSize: '0.85rem' }} onClick={() => navigate(`/meetings/${m.id}`)}>
                  View Workflow & Roles
                </Button>
              </div>
            </Card>
          ))}

          {filteredMeetings.length === 0 && (
            <Card style={{ gridColumn: '1 / -1', textAlign: 'center', padding: 40, color: '#64748B' }}>
              No meetings found for the selected status.
            </Card>
          )}
        </div>
      )}

      {/* Schedule Meeting Modal */}
      <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} title="Schedule New Meeting">
        <form onSubmit={handleCreateMeeting}>
          <div className="form-group">
            <label className="form-label">Meeting Number</label>
            <input
              type="number"
              required
              className="form-input"
              value={meetingNumber}
              onChange={(e) => setMeetingNumber(parseInt(e.target.value))}
            />
          </div>

          <div className="form-group">
            <label className="form-label">Meeting Start Date & Time</label>
            <input
              type="datetime-local"
              required
              className="form-input"
              value={meetingStart}
              onChange={(e) => setMeetingStart(e.target.value)}
            />
          </div>

          <div className="form-group">
            <label className="form-label">Theme</label>
            <input
              type="text"
              placeholder="e.g. Navigating the Future"
              className="form-input"
              value={theme}
              onChange={(e) => setTheme(e.target.value)}
            />
          </div>

          <div className="form-group">
            <label className="form-label">Meeting Type</label>
            <select
              className="form-select"
              value={meetingType}
              onChange={(e) => setMeetingType(e.target.value as MeetingType)}
            >
              <option value="REGULAR">REGULAR</option>
              <option value="SPECIAL">SPECIAL</option>
              <option value="CONTEST">CONTEST</option>
            </select>
          </div>

          <div className="form-group">
            <label className="form-label">Location</label>
            <input
              type="text"
              className="form-input"
              value={location}
              onChange={(e) => setLocation(e.target.value)}
            />
          </div>

          <Button type="submit" variant="gold" style={{ width: '100%', marginTop: 10 }} disabled={creating}>
            {creating ? 'Scheduling...' : 'Confirm & Schedule Meeting'}
          </Button>
        </form>
      </Modal>
    </AppLayout>
  );
};
