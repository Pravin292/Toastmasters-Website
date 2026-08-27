import React, { useEffect, useState } from 'react';
import { AppLayout } from '../components/layout/AppLayout';
import { Card } from '../components/common/Card';
import { Badge as UIBadge } from '../components/common/Badge';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { ErrorMessage } from '../components/common/ErrorMessage';
import { useAuth } from '../context/AuthContext';
import { achievementApi } from '../api/achievementApi';
import { memberApi } from '../api/memberApi';
import { Achievement, Badge as BadgeType, Certificate, Member, IssueCertificateRequest } from '../types';
import { Award, FileCheck, RefreshCw, Plus, Eye, CheckCircle, ShieldCheck, Sparkles, X } from 'lucide-react';

export const AchievementsPage: React.FC = () => {
  const { user } = useAuth();
  const [loading, setLoading] = useState(true);
  const [evaluating, setEvaluating] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  const [achievements, setAchievements] = useState<Achievement[]>([]);
  const [badges, setBadges] = useState<BadgeType[]>([]);
  const [certificates, setCertificates] = useState<Certificate[]>([]);

  // Certificate Viewer Modal State
  const [selectedCertificate, setSelectedCertificate] = useState<Certificate | null>(null);

  // Officer Issue Certificate Modal State
  const [showIssueModal, setShowIssueModal] = useState(false);
  const [members, setMembers] = useState<Member[]>([]);
  const [issuing, setIssuing] = useState(false);

  const [issueForm, setIssueForm] = useState<IssueCertificateRequest>({
    memberId: '',
    certificateType: 'SPECIAL_RECOGNITION',
    title: '',
    description: '',
  });

  const isOfficer = ['ADMIN', 'PRESIDENT', 'OFFICER'].includes(user?.role || '');

  const loadData = async () => {
    if (!user?.memberId) {
      setLoading(false);
      return;
    }
    setLoading(true);
    setError(null);
    try {
      const [achRes, badgeRes, certRes] = await Promise.all([
        achievementApi.getMemberAchievements(user.memberId).catch(() => []),
        achievementApi.getMemberBadges(user.memberId).catch(() => []),
        achievementApi.getMemberCertificates(user.memberId).catch(() => []),
      ]);
      setAchievements(achRes);
      setBadges(badgeRes);
      setCertificates(certRes);
    } catch (err: any) {
      setError(err.message || 'Failed to load achievements');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [user]);

  const handleEvaluate = async () => {
    if (!user?.memberId) return;
    setEvaluating(true);
    setError(null);
    setSuccessMessage(null);
    try {
      await achievementApi.evaluateMemberAchievements(user.memberId);
      setSuccessMessage('Achievement evaluation completed! Any newly unlocked recognitions have been awarded.');
      await loadData();
    } catch (err: any) {
      setError(err.message || 'Failed to evaluate achievements');
    } finally {
      setEvaluating(false);
    }
  };

  const handleOpenIssueModal = async () => {
    setShowIssueModal(true);
    if (members.length === 0) {
      try {
        const memberList = await memberApi.getMembers();
        setMembers(memberList.filter((m) => m.status === 'ACTIVE' || !m.status));
        if (memberList.length > 0) {
          setIssueForm((prev) => ({ ...prev, memberId: memberList[0].id }));
        }
      } catch (err: any) {
        setError('Failed to fetch members for certificate issuance');
      }
    }
  };

  const handleIssueCertificate = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!issueForm.memberId || !issueForm.title || !issueForm.description) {
      setError('Please fill in all required certificate fields');
      return;
    }
    setIssuing(true);
    setError(null);
    try {
      await achievementApi.issueCertificate(issueForm);
      setSuccessMessage(`Certificate "${issueForm.title}" issued successfully!`);
      setShowIssueModal(false);
      setIssueForm({
        memberId: members[0]?.id || '',
        certificateType: 'SPECIAL_RECOGNITION',
        title: '',
        description: '',
      });
      await loadData();
    } catch (err: any) {
      setError(err.message || 'Failed to issue certificate');
    } finally {
      setIssuing(false);
    }
  };

  if (loading) {
    return (
      <AppLayout title="Achievements, Badges & Certificates">
        <LoadingSpinner />
      </AppLayout>
    );
  }

  return (
    <AppLayout title="Achievements, Badges & Certificates">
      {error && <ErrorMessage message={error} />}

      {successMessage && (
        <div style={{
          background: 'rgba(16, 185, 129, 0.15)',
          border: '1px solid rgba(16, 185, 129, 0.4)',
          color: '#10B981',
          padding: '12px 18px',
          borderRadius: 12,
          marginBottom: 20,
          display: 'flex',
          alignItems: 'center',
          gap: 10,
          fontSize: '0.9rem'
        }}>
          <CheckCircle size={18} />
          <span>{successMessage}</span>
        </div>
      )}

      {/* Header Actions */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 28, flexWrap: 'wrap', gap: 12 }}>
        <div>
          <h2 style={{ fontSize: '1.4rem', color: '#FFF', display: 'flex', alignItems: 'center', gap: 8 }}>
            <Sparkles size={22} color="#F2DF74" />
            <span>Club Performance Recognitions</span>
          </h2>
          <p style={{ fontSize: '0.85rem', color: '#94A3B8' }}>
            Earn club-level badges and certificates based on your meeting attendance, role participation, and points achievements.
          </p>
        </div>

        <div style={{ display: 'flex', gap: 10 }}>
          <button
            type="button"
            className="btn btn-secondary"
            onClick={handleEvaluate}
            disabled={evaluating}
            style={{ display: 'flex', alignItems: 'center', gap: 6, padding: '8px 14px', fontSize: '0.85rem' }}
          >
            <RefreshCw size={16} className={evaluating ? 'spin' : ''} />
            <span>{evaluating ? 'Evaluating...' : 'Evaluate Achievements'}</span>
          </button>

          {isOfficer && (
            <button
              type="button"
              className="btn btn-primary"
              onClick={handleOpenIssueModal}
              style={{ display: 'flex', alignItems: 'center', gap: 6, padding: '8px 14px', fontSize: '0.85rem' }}
            >
              <Plus size={16} />
              <span>Issue Certificate</span>
            </button>
          )}
        </div>
      </div>

      {/* PHASE 2: Visual Badges Grid */}
      <div style={{ marginBottom: 36 }}>
        <h3 style={{ fontSize: '1.15rem', marginBottom: 16, display: 'flex', alignItems: 'center', gap: 8 }}>
          <ShieldCheck size={20} color="#F2DF74" />
          <span>Visual Badges ({badges.length})</span>
        </h3>

        <div className="card-grid" style={{ gridTemplateColumns: 'repeat(auto-fill, minmax(250px, 1fr))' }}>
          {badges.map((b, idx) => (
            <Card key={b.achievementId || idx} style={{ textAlign: 'center', padding: 20 }}>
              <div style={{
                width: 60,
                height: 60,
                borderRadius: '50%',
                background: 'linear-gradient(135deg, rgba(242, 223, 116, 0.2) 0%, rgba(119, 33, 111, 0.3) 100%)',
                border: '1px solid rgba(242, 223, 116, 0.5)',
                margin: '0 auto 12px auto',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                fontSize: '2rem'
              }}>
                {b.icon || '🏅'}
              </div>
              <h4 style={{ fontSize: '1.05rem', color: '#FFF', marginBottom: 4 }}>{b.badgeName}</h4>
              <p style={{ fontSize: '0.82rem', color: '#94A3B8', marginBottom: 12, minHeight: 36 }}>
                {b.description}
              </p>
              <div style={{ display: 'flex', justifyContent: 'center', gap: 6, flexWrap: 'wrap' }}>
                <UIBadge variant="completed">
                  {b.category || 'CLUB PERFORMANCE'}
                </UIBadge>
                {b.earnedAt && (
                  <span style={{ fontSize: '0.75rem', color: '#60A5FA', display: 'block', width: '100%', marginTop: 4 }}>
                    Unlocked {new Date(b.earnedAt).toLocaleDateString()}
                  </span>
                )}
              </div>
            </Card>
          ))}

          {badges.length === 0 && (
            <Card style={{ gridColumn: '1 / -1', textAlign: 'center', padding: 28, color: '#64748B' }}>
              No visual badges unlocked yet. Attend club meetings and perform meeting roles to earn badges!
            </Card>
          )}
        </div>
      </div>

      {/* PHASE 1: Club Performance Achievements List */}
      <div style={{ marginBottom: 36 }}>
        <h3 style={{ fontSize: '1.15rem', marginBottom: 16, display: 'flex', alignItems: 'center', gap: 8 }}>
          <Award size={20} color="#10B981" />
          <span>Unlocked Achievements ({achievements.length})</span>
        </h3>

        <div className="table-container">
          <table>
            <thead>
              <tr>
                <th>Icon</th>
                <th>Achievement Name</th>
                <th>Category</th>
                <th>Reason / Description</th>
                <th>Earned Date</th>
              </tr>
            </thead>
            <tbody>
              {achievements.map((ach) => (
                <tr key={ach.id}>
                  <td style={{ fontSize: '1.6rem', textAlign: 'center', width: 60 }}>{ach.icon || '🏅'}</td>
                  <td style={{ fontWeight: 700, color: '#F2DF74' }}>
                    {ach.achievementName || ach.name || 'Club Achievement'}
                  </td>
                  <td>
                    <UIBadge variant="in_progress">{ach.category || 'CLUB'}</UIBadge>
                  </td>
                  <td style={{ fontSize: '0.88rem', color: '#94A3B8' }}>
                    {ach.reason || ach.description || 'Recognized for active participation'}
                  </td>
                  <td style={{ fontSize: '0.85rem', color: '#60A5FA' }}>
                    {ach.earnedAt ? new Date(ach.earnedAt).toLocaleDateString() : '-'}
                  </td>
                </tr>
              ))}

              {achievements.length === 0 && (
                <tr>
                  <td colSpan={5} style={{ textAlign: 'center', color: '#64748B', padding: 24 }}>
                    No achievements recorded for your account yet. Click "Evaluate Achievements" above to scan for unlocked milestones!
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* PHASE 3: Club Performance Certificates */}
      <div>
        <h3 style={{ fontSize: '1.15rem', marginBottom: 16, display: 'flex', alignItems: 'center', gap: 8 }}>
          <FileCheck size={20} color="#60A5FA" />
          <span>Club Performance Certificates ({certificates.length})</span>
        </h3>

        <div className="table-container">
          <table>
            <thead>
              <tr>
                <th>Certificate #</th>
                <th>Certificate Title</th>
                <th>Type</th>
                <th>Description</th>
                <th>Issued Date</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              {certificates.map((cert) => (
                <tr key={cert.id}>
                  <td style={{ fontWeight: 700, color: '#60A5FA', fontFamily: 'monospace' }}>
                    {cert.certificateNumber}
                  </td>
                  <td style={{ fontWeight: 600, color: '#FFF' }}>{cert.title}</td>
                  <td>
                    <UIBadge variant="scheduled">{cert.certificateType}</UIBadge>
                  </td>
                  <td style={{ fontSize: '0.85rem', color: '#94A3B8' }}>
                    {cert.description || 'Awarded for club milestone performance'}
                  </td>
                  <td style={{ fontSize: '0.85rem', color: '#94A3B8' }}>
                    {cert.issuedDate ? new Date(cert.issuedDate).toLocaleDateString() : '-'}
                  </td>
                  <td>
                    <button
                      className="btn btn-secondary"
                      style={{ padding: '6px 12px', fontSize: '0.8rem', display: 'flex', alignItems: 'center', gap: 4 }}
                      onClick={() => setSelectedCertificate(cert)}
                    >
                      <Eye size={14} /> View Certificate
                    </button>
                  </td>
                </tr>
              ))}

              {certificates.length === 0 && (
                <tr>
                  <td colSpan={6} style={{ textAlign: 'center', color: '#64748B', padding: 24 }}>
                    No club performance certificates issued to your account yet.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* CERTIFICATE VIEWER MODAL */}
      {selectedCertificate && (
        <div style={{
          position: 'fixed',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          background: 'rgba(0, 0, 0, 0.8)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          zIndex: 1000,
          padding: 20
        }}>
          <div style={{
            background: '#0F172A',
            border: '2px solid #F2DF74',
            borderRadius: 16,
            maxWidth: 650,
            width: '100%',
            padding: 32,
            position: 'relative',
            boxShadow: '0 12px 40px rgba(0,0,0,0.6)'
          }}>
            <button
              onClick={() => setSelectedCertificate(null)}
              style={{ position: 'absolute', top: 16, right: 16, background: 'none', border: 'none', color: '#94A3B8', cursor: 'pointer' }}
            >
              <X size={24} />
            </button>

            {/* Certificate Presentation Document */}
            <div style={{
              border: '2px dashed rgba(242, 223, 116, 0.5)',
              padding: 28,
              borderRadius: 12,
              textAlign: 'center',
              background: 'linear-gradient(135deg, rgba(242, 223, 116, 0.05) 0%, rgba(119, 33, 111, 0.1) 100%)'
            }}>
              <div style={{ fontSize: '0.8rem', letterSpacing: '0.1em', textTransform: 'uppercase', color: '#F2DF74', fontWeight: 700, marginBottom: 8 }}>
                Rathinam Toastmasters Club Recognition
              </div>
              <h2 style={{ fontSize: '1.8rem', color: '#FFF', fontFamily: 'serif', marginBottom: 12 }}>
                {selectedCertificate.title}
              </h2>
              <div style={{ fontSize: '0.9rem', color: '#94A3B8', marginBottom: 16 }}>
                This Club Performance Certificate is proudly presented to
              </div>
              <h3 style={{ fontSize: '1.6rem', color: '#F2DF74', marginBottom: 16 }}>
                {selectedCertificate.memberDisplayName || (user?.firstName ? `${user.firstName} ${user.lastName || ''}`.trim() : 'Club Member')}
              </h3>
              <p style={{ fontSize: '0.92rem', color: '#E2E8F0', lineHeight: 1.5, marginBottom: 24 }}>
                {selectedCertificate.description || 'In recognition of outstanding performance, participation, and contribution to Rathinam Toastmasters.'}
              </p>

              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end', marginTop: 24, paddingTop: 16, borderTop: '1px solid rgba(255,255,255,0.1)', fontSize: '0.8rem', color: '#94A3B8' }}>
                <div>
                  <div>Certificate #: <strong style={{ color: '#60A5FA', fontFamily: 'monospace' }}>{selectedCertificate.certificateNumber}</strong></div>
                  <div>Issued Date: {new Date(selectedCertificate.issuedDate).toLocaleDateString()}</div>
                </div>
                <div style={{ textAlign: 'right' }}>
                  <div style={{ borderBottom: '1px solid #94A3B8', paddingBottom: 4, marginBottom: 4, fontWeight: 700, color: '#FFF' }}>
                    Rathinam Toastmasters Executive
                  </div>
                  <div>Club Performance Award</div>
                </div>
              </div>
            </div>

            <div style={{ marginTop: 20, textAlign: 'right' }}>
              <button
                className="btn btn-secondary"
                onClick={() => setSelectedCertificate(null)}
              >
                Close
              </button>
            </div>
          </div>
        </div>
      )}

      {/* OFFICER ISSUE CERTIFICATE MODAL */}
      {showIssueModal && (
        <div style={{
          position: 'fixed',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          background: 'rgba(0, 0, 0, 0.8)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          zIndex: 1000,
          padding: 20
        }}>
          <div style={{
            background: '#0F172A',
            border: '1px solid var(--bg-card-border)',
            borderRadius: 16,
            maxWidth: 500,
            width: '100%',
            padding: 24,
            position: 'relative'
          }}>
            <button
              onClick={() => setShowIssueModal(false)}
              style={{ position: 'absolute', top: 16, right: 16, background: 'none', border: 'none', color: '#94A3B8', cursor: 'pointer' }}
            >
              <X size={20} />
            </button>

            <h3 style={{ fontSize: '1.2rem', color: '#FFF', marginBottom: 16, display: 'flex', alignItems: 'center', gap: 8 }}>
              <FileCheck size={20} color="#F2DF74" />
              <span>Issue Club Performance Certificate</span>
            </h3>

            <form onSubmit={handleIssueCertificate}>
              <div className="form-group">
                <label className="form-label">Recipient Member</label>
                <select
                  className="form-select"
                  value={issueForm.memberId}
                  onChange={(e) => setIssueForm({ ...issueForm, memberId: e.target.value })}
                  required
                >
                  {members.map((m) => (
                    <option key={m.id} value={m.id}>
                      {m.displayName || `${m.firstName} ${m.lastName}`} ({m.email})
                    </option>
                  ))}
                </select>
              </div>

              <div className="form-group">
                <label className="form-label">Certificate Type</label>
                <select
                  className="form-select"
                  value={issueForm.certificateType}
                  onChange={(e) => setIssueForm({ ...issueForm, certificateType: e.target.value })}
                  required
                >
                  <option value="MONTHLY_CHAMPION">Monthly Champion</option>
                  <option value="MILESTONE_ATTENDANCE">Milestone Attendance</option>
                  <option value="MILESTONE_ROLE">Milestone Role Participation</option>
                  <option value="MILESTONE_POINTS">Milestone Total Points</option>
                  <option value="SPECIAL_RECOGNITION">Special Recognition</option>
                </select>
              </div>

              <div className="form-group">
                <label className="form-label">Certificate Title</label>
                <input
                  type="text"
                  className="form-control"
                  placeholder="e.g. Certificate of Outstanding Participation"
                  value={issueForm.title}
                  onChange={(e) => setIssueForm({ ...issueForm, title: e.target.value })}
                  required
                />
              </div>

              <div className="form-group">
                <label className="form-label">Description / Citation</label>
                <textarea
                  className="form-control"
                  rows={3}
                  placeholder="e.g. Awarded for completing 10 meeting roles with exemplary dedication."
                  value={issueForm.description}
                  onChange={(e) => setIssueForm({ ...issueForm, description: e.target.value })}
                  required
                />
              </div>

              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 12, marginTop: 24 }}>
                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={() => setShowIssueModal(false)}
                  disabled={issuing}
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="btn btn-primary"
                  disabled={issuing}
                >
                  {issuing ? 'Issuing...' : 'Issue Certificate'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </AppLayout>
  );
};
