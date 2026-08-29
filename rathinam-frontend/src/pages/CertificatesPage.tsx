import React, { useEffect, useState } from 'react';
import { AppLayout } from '../components/layout/AppLayout';
import { Card } from '../components/common/Card';
import { Badge } from '../components/common/Badge';
import { Button } from '../components/common/Button';
import { Modal } from '../components/common/Modal';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { ErrorMessage } from '../components/common/ErrorMessage';
import { useAuth } from '../context/AuthContext';
import { achievementApi } from '../api/achievementApi';
import { memberApi } from '../api/memberApi';
import { Certificate, Member } from '../types';
import { ShieldCheck, Award, Plus, Calendar, CheckCircle2 } from 'lucide-react';

export const CertificatesPage: React.FC = () => {
  const { user, isOfficer } = useAuth();

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [certificates, setCertificates] = useState<Certificate[]>([]);
  const [members, setMembers] = useState<Member[]>([]);

  // Issue Certificate Modal State
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedMemberId, setSelectedMemberId] = useState('');
  const [title, setTitle] = useState('');
  const [certificateType, setCertificateType] = useState('BEST_SPEAKER');
  const [description, setDescription] = useState('');
  const [issuing, setIssuing] = useState(false);

  const fetchCertificates = async () => {
    setLoading(true);
    setError(null);
    try {
      if (user?.memberId) {
        const certRes = await achievementApi.getMemberCertificates(user.memberId);
        setCertificates(certRes);
      }
      if (isOfficer) {
        const memRes = await memberApi.getMembers().catch(() => []);
        setMembers(memRes);
      }
    } catch (err: any) {
      setError(err.message || 'Failed to fetch certificates');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCertificates();
  }, [user]);

  const handleIssueCertificate = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedMemberId) return;
    setIssuing(true);
    setError(null);
    try {
      await achievementApi.issueCertificate({
        memberId: selectedMemberId,
        title,
        certificateType,
        description
      });
      setIsModalOpen(false);
      setTitle('');
      setDescription('');
      fetchCertificates();
    } catch (err: any) {
      setError(err.message || 'Failed to issue certificate');
    } finally {
      setIssuing(false);
    }
  };

  return (
    <AppLayout title="Certificates & Credentials">
      {/* Top Header Controls */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 28, flexWrap: 'wrap', gap: 16 }}>
        <div>
          <h2 style={{ fontSize: '1.4rem', fontWeight: 800, color: '#FFFFFF', display: 'flex', alignItems: 'center', gap: 10 }}>
            <ShieldCheck size={24} color="#38BDF8" />
            <span>Official Recognized Certificates</span>
          </h2>
          <p style={{ fontSize: '0.88rem', color: '#94A3B8', marginTop: 4 }}>
            Verified Toastmasters credentials issued for meeting roles, speeches, and achievements
          </p>
        </div>

        {isOfficer && (
          <Button variant="gold" onClick={() => setIsModalOpen(true)}>
            <Plus size={18} /> Issue Certificate
          </Button>
        )}
      </div>

      {error && <ErrorMessage message={error} />}

      {loading ? (
        <LoadingSpinner />
      ) : (
        <div className="card-grid" style={{ gridTemplateColumns: 'repeat(auto-fill, minmax(320px, 1fr))', gap: 24 }}>
          {certificates.map((cert) => (
            <Card key={cert.id} style={{ position: 'relative', overflow: 'hidden' }}>
              <div style={{
                position: 'absolute',
                top: -10,
                right: -10,
                width: 80,
                height: 80,
                borderRadius: '50%',
                background: 'radial-gradient(circle, rgba(56, 189, 248, 0.15) 0%, transparent 70%)',
                pointerEvents: 'none'
              }} />

              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 16 }}>
                <div style={{
                  width: 48,
                  height: 48,
                  borderRadius: 14,
                  background: 'rgba(56, 189, 248, 0.12)',
                  border: '1px solid rgba(56, 189, 248, 0.25)',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  color: '#38BDF8'
                }}>
                  <Award size={24} />
                </div>
                <Badge variant="completed">VERIFIED</Badge>
              </div>

              <h3 style={{ fontSize: '1.2rem', fontWeight: 800, color: '#FFFFFF', marginBottom: 6 }}>
                {cert.title}
              </h3>
              <p style={{ fontSize: '0.86rem', color: '#94A3B8', marginBottom: 18, lineHeight: 1.4 }}>
                {cert.description || 'Awarded for excellence in Toastmasters speech performance.'}
              </p>

              <div style={{ display: 'flex', flexDirection: 'column', gap: 8, fontSize: '0.82rem', color: '#64748B', paddingTop: 14, borderTop: '1px solid rgba(255, 255, 255, 0.08)' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
                  <Calendar size={14} color="#34D399" />
                  <span>Issued on {new Date(cert.issuedDate).toLocaleDateString()}</span>
                </div>
                <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
                  <CheckCircle2 size={14} color="#38BDF8" />
                  <span>Credential ID: {cert.certificateNumber || cert.id.substring(0, 10)}</span>
                </div>
              </div>
            </Card>
          ))}

          {certificates.length === 0 && (
            <Card style={{ gridColumn: '1 / -1', textAlign: 'center', padding: 50, color: '#64748B' }}>
              No certificates issued yet. Complete speech roles and achieve meeting milestones to earn official certificates!
            </Card>
          )}
        </div>
      )}

      {/* Issue Certificate Modal */}
      <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} title="Issue Official Certificate">
        <form onSubmit={handleIssueCertificate}>
          <div className="form-group">
            <label className="form-label">Select Member</label>
            <select
              className="form-select"
              required
              value={selectedMemberId}
              onChange={(e) => setSelectedMemberId(e.target.value)}
            >
              <option value="">-- Choose Member --</option>
              {members.map((m) => (
                <option key={m.id} value={m.id}>
                  {m.displayName || `${m.firstName} ${m.lastName}`} ({m.email})
                </option>
              ))}
            </select>
          </div>

          <div className="form-group">
            <label className="form-label">Certificate Title</label>
            <input
              type="text"
              required
              placeholder="e.g. Best Evaluator of the Month"
              className="form-input"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
            />
          </div>

          <div className="form-group">
            <label className="form-label">Category / Type</label>
            <select
              className="form-select"
              value={certificateType}
              onChange={(e) => setCertificateType(e.target.value)}
            >
              <option value="BEST_SPEAKER">BEST_SPEAKER</option>
              <option value="BEST_EVALUATOR">BEST_EVALUATOR</option>
              <option value="BEST_TABLE_TOPICS">BEST_TABLE_TOPICS</option>
              <option value="MILESTONE_SPEECH">MILESTONE_SPEECH</option>
              <option value="EXCOM_EXCELLENCE">EXCOM_EXCELLENCE</option>
            </select>
          </div>

          <div className="form-group">
            <label className="form-label">Description / Citation</label>
            <textarea
              className="form-input"
              rows={3}
              placeholder="Citation details for the award..."
              value={description}
              onChange={(e) => setDescription(e.target.value)}
            />
          </div>

          <Button type="submit" variant="gold" style={{ width: '100%', marginTop: 10 }} disabled={issuing}>
            {issuing ? 'Issuing Certificate...' : 'Confirm & Issue Certificate'}
          </Button>
        </form>
      </Modal>
    </AppLayout>
  );
};
