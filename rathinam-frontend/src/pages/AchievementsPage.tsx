import React, { useEffect, useState } from 'react';
import { AppLayout } from '../components/layout/AppLayout';
import { Card } from '../components/common/Card';
import { Badge } from '../components/common/Badge';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { ErrorMessage } from '../components/common/ErrorMessage';
import { useAuth } from '../context/AuthContext';
import { achievementApi } from '../api/achievementApi';
import { Achievement, Certificate } from '../types';
import { Award, FileCheck, Download } from 'lucide-react';

export const AchievementsPage: React.FC = () => {
  const { user } = useAuth();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [achievements, setAchievements] = useState<Achievement[]>([]);
  const [certificates, setCertificates] = useState<Certificate[]>([]);

  useEffect(() => {
    const fetchAchievementsData = async () => {
      if (!user?.memberId) {
        setLoading(false);
        return;
      }
      setLoading(true);
      setError(null);
      try {
        const [achRes, certRes] = await Promise.all([
          achievementApi.getMemberAchievements(user.memberId).catch(() => []),
          achievementApi.getMemberCertificates(user.memberId).catch(() => []),
        ]);
        setAchievements(achRes);
        setCertificates(certRes);
      } catch (err: any) {
        setError(err.message || 'Failed to load achievements');
      } finally {
        setLoading(false);
      }
    };

    fetchAchievementsData();
  }, [user]);

  if (loading) {
    return (
      <AppLayout title="Badges & Certificates">
        <LoadingSpinner />
      </AppLayout>
    );
  }

  return (
    <AppLayout title="Badges & Certificates">
      {error && <ErrorMessage message={error} />}

      {/* Badges Section */}
      <div style={{ marginBottom: 32 }}>
        <h3 style={{ fontSize: '1.2rem', marginBottom: 16, display: 'flex', alignItems: 'center', gap: 8 }}>
          <Award size={20} color="#F2DF74" />
          <span>Earned Badges ({achievements.length})</span>
        </h3>

        <div className="card-grid" style={{ gridTemplateColumns: 'repeat(auto-fill, minmax(260px, 1fr))' }}>
          {achievements.map((ach) => (
            <Card key={ach.id} style={{ textAlign: 'center', padding: 24 }}>
              <div style={{ fontSize: '3rem', marginBottom: 12 }}>{ach.icon || '🏅'}</div>
              <h4 style={{ fontSize: '1.1rem', marginBottom: 6 }}>{ach.name}</h4>
              <p style={{ fontSize: '0.85rem', color: '#94A3B8', marginBottom: 12 }}>
                {ach.description}
              </p>
              {ach.earnedAt && (
                <Badge variant="completed">
                  Earned {new Date(ach.earnedAt).toLocaleDateString()}
                </Badge>
              )}
            </Card>
          ))}

          {achievements.length === 0 && (
            <Card style={{ gridColumn: '1 / -1', textAlign: 'center', padding: 32, color: '#64748B' }}>
              No badges earned yet. Participate in meetings to unlock recognitions!
            </Card>
          )}
        </div>
      </div>

      {/* Certificates Section */}
      <div>
        <h3 style={{ fontSize: '1.2rem', marginBottom: 16, display: 'flex', alignItems: 'center', gap: 8 }}>
          <FileCheck size={20} color="#60A5FA" />
          <span>Official Certificates ({certificates.length})</span>
        </h3>

        <div className="table-container">
          <table>
            <thead>
              <tr>
                <th>Certificate #</th>
                <th>Title</th>
                <th>Type</th>
                <th>Issued Date</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              {certificates.map((cert) => (
                <tr key={cert.id}>
                  <td style={{ fontWeight: 700, color: '#60A5FA' }}>{cert.certificateNumber}</td>
                  <td style={{ fontWeight: 600 }}>{cert.title}</td>
                  <td><Badge variant="scheduled">{cert.certificateType}</Badge></td>
                  <td style={{ fontSize: '0.88rem', color: '#94A3B8' }}>{new Date(cert.issuedDate).toLocaleDateString()}</td>
                  <td>
                    <button
                      className="btn btn-secondary"
                      style={{ padding: '6px 12px', fontSize: '0.8rem' }}
                      onClick={() => alert(`Certificate #${cert.certificateNumber}: ${cert.title}`)}
                    >
                      <Download size={14} /> Download PDF
                    </button>
                  </td>
                </tr>
              ))}

              {certificates.length === 0 && (
                <tr>
                  <td colSpan={5} style={{ textAlign: 'center', color: '#64748B', padding: 28 }}>
                    No certificates issued to your account yet.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </AppLayout>
  );
};
