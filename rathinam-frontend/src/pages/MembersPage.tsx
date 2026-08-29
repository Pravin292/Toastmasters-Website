import React, { useEffect, useState } from 'react';
import { AppLayout } from '../components/layout/AppLayout';
import { Card } from '../components/common/Card';
import { Badge } from '../components/common/Badge';
import { Button } from '../components/common/Button';
import { Modal } from '../components/common/Modal';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { ErrorMessage } from '../components/common/ErrorMessage';
import { useAuth } from '../context/AuthContext';
import { memberApi } from '../api/memberApi';
import { Member } from '../types';
import { Users, UserPlus, Search, Mail, Calendar, Phone, Shield } from 'lucide-react';

export const MembersPage: React.FC = () => {
  const { isOfficer } = useAuth();

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [members, setMembers] = useState<Member[]>([]);
  const [searchQuery, setSearchQuery] = useState('');

  // Create Modal State
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [email, setEmail] = useState('');
  const [joinDate, setJoinDate] = useState(new Date().toISOString().split('T')[0]);
  const [creating, setCreating] = useState(false);

  const fetchMembers = async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await memberApi.getMembers();
      setMembers(res);
    } catch (err: any) {
      setError(err.message || 'Failed to fetch club members');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchMembers();
  }, []);

  const handleCreateMember = async (e: React.FormEvent) => {
    e.preventDefault();
    setCreating(true);
    setError(null);
    try {
      await memberApi.createMember({
        firstName,
        lastName,
        email,
        joinDate: new Date(joinDate).toISOString()
      });
      setIsModalOpen(false);
      setFirstName('');
      setLastName('');
      setEmail('');
      fetchMembers();
    } catch (err: any) {
      setError(err.message || 'Failed to create member');
    } finally {
      setCreating(false);
    }
  };

  const filteredMembers = members.filter(m =>
    `${m.firstName} ${m.lastName}`.toLowerCase().includes(searchQuery.toLowerCase()) ||
    m.email.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <AppLayout title="Club Members">
      {/* Top Action Header */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 28, flexWrap: 'wrap', gap: 16 }}>
        <div style={{ position: 'relative', width: 320, maxWidth: '100%' }}>
          <Search size={16} color="#94A3B8" style={{ position: 'absolute', left: 14, top: '50%', transform: 'translateY(-50%)' }} />
          <input
            type="text"
            placeholder="Search member name or email..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            style={{
              width: '100%',
              padding: '10px 14px 10px 40px',
              borderRadius: 12,
              background: 'rgba(15, 23, 42, 0.75)',
              border: '1px solid rgba(255, 255, 255, 0.1)',
              color: '#F8FAFC',
              fontSize: '0.9rem'
            }}
          />
        </div>

        {isOfficer && (
          <Button variant="gold" onClick={() => setIsModalOpen(true)}>
            <UserPlus size={18} /> Register New Member
          </Button>
        )}
      </div>

      {error && <ErrorMessage message={error} />}

      {loading ? (
        <LoadingSpinner />
      ) : (
        <div className="card-grid" style={{ gridTemplateColumns: 'repeat(auto-fill, minmax(290px, 1fr))', gap: 20 }}>
          {filteredMembers.map((m) => (
            <Card key={m.id} style={{ display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
              <div>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 14 }}>
                  <div style={{
                    width: 44,
                    height: 44,
                    borderRadius: 14,
                    background: 'linear-gradient(135deg, rgba(56, 189, 248, 0.2) 0%, rgba(37, 99, 235, 0.2) 100%)',
                    border: '1px solid rgba(56, 189, 248, 0.3)',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    fontSize: '1.1rem',
                    fontWeight: 800,
                    color: '#38BDF8'
                  }}>
                    {m.firstName ? m.firstName.charAt(0) : 'M'}
                  </div>
                  <Badge variant={m.status === 'ACTIVE' ? 'completed' : 'default'}>
                    {m.status || 'ACTIVE'}
                  </Badge>
                </div>

                <h3 style={{ fontSize: '1.15rem', fontWeight: 800, color: '#FFFFFF', marginBottom: 4 }}>
                  {m.displayName || `${m.firstName} ${m.lastName}`}
                </h3>

                <div style={{ display: 'flex', flexDirection: 'column', gap: 8, fontSize: '0.86rem', color: '#94A3B8', marginTop: 12 }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                    <Mail size={15} color="#38BDF8" />
                    <span>{m.email}</span>
                  </div>

                  {m.joinDate && (
                    <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                      <Calendar size={15} color="#34D399" />
                      <span>Member since {new Date(m.joinDate).toLocaleDateString()}</span>
                    </div>
                  )}

                  {m.phoneNumber && (
                    <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                      <Phone size={15} color="#FBBF24" />
                      <span>{m.phoneNumber}</span>
                    </div>
                  )}
                </div>
              </div>

              <div style={{ marginTop: 16, paddingTop: 14, borderTop: '1px solid rgba(255, 255, 255, 0.08)', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <span style={{ fontSize: '0.78rem', color: '#64748B' }}>Rathinam Toastmasters</span>
                <span style={{ fontSize: '0.78rem', fontWeight: 700, color: '#38BDF8' }}>ID: {m.id.substring(0, 8)}...</span>
              </div>
            </Card>
          ))}

          {filteredMembers.length === 0 && (
            <Card style={{ gridColumn: '1 / -1', textAlign: 'center', padding: 40, color: '#64748B' }}>
              No club members found.
            </Card>
          )}
        </div>
      )}

      {/* Add Member Modal */}
      <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} title="Register New Toastmasters Member">
        <form onSubmit={handleCreateMember}>
          <div className="form-group">
            <label className="form-label">First Name</label>
            <input
              type="text"
              required
              className="form-input"
              value={firstName}
              onChange={(e) => setFirstName(e.target.value)}
            />
          </div>

          <div className="form-group">
            <label className="form-label">Last Name</label>
            <input
              type="text"
              required
              className="form-input"
              value={lastName}
              onChange={(e) => setLastName(e.target.value)}
            />
          </div>

          <div className="form-group">
            <label className="form-label">Email Address</label>
            <input
              type="email"
              required
              className="form-input"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
          </div>

          <div className="form-group">
            <label className="form-label">Join Date</label>
            <input
              type="date"
              required
              className="form-input"
              value={joinDate}
              onChange={(e) => setJoinDate(e.target.value)}
            />
          </div>

          <Button type="submit" variant="gold" style={{ width: '100%', marginTop: 10 }} disabled={creating}>
            {creating ? 'Registering...' : 'Confirm Registration'}
          </Button>
        </form>
      </Modal>
    </AppLayout>
  );
};
