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
import { roleApi } from '../api/roleApi';
import { pointApi } from '../api/pointApi';
import { achievementApi } from '../api/achievementApi';
import { RoleDefinition, PointRule, Member } from '../types';
import { Shield, Plus, UserPlus, Award, FileCheck, Eye, Edit } from 'lucide-react';

export const AdminPage: React.FC = () => {
  const { isOfficer } = useAuth();

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [successMsg, setSuccessMsg] = useState<string | null>(null);

  const [roleDefs, setRoleDefs] = useState<RoleDefinition[]>([]);
  const [pointRules, setPointRules] = useState<PointRule[]>([]);
  const [members, setMembers] = useState<Member[]>([]);

  // Modals
  const [isMemberModalOpen, setIsMemberModalOpen] = useState(false);
  const [isRoleModalOpen, setIsRoleModalOpen] = useState(false);
  const [isPointModalOpen, setIsPointModalOpen] = useState(false);
  const [isCertModalOpen, setIsCertModalOpen] = useState(false);

  const getTodayDate = () => new Date().toISOString().split('T')[0];

  // View & Edit Member State
  const [isViewModalOpen, setIsViewModalOpen] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [selectedMember, setSelectedMember] = useState<Member | null>(null);

  const [editForm, setEditForm] = useState({
    firstName: '',
    lastName: '',
    displayName: '',
    email: '',
    phoneNumber: '',
    joinDate: '',
    status: 'ACTIVE',
    bio: '',
  });

  const handleOpenViewModal = (member: Member) => {
    setSelectedMember(member);
    setIsViewModalOpen(true);
  };

  const handleOpenEditModal = (member: Member) => {
    setSelectedMember(member);
    setEditForm({
      firstName: member.firstName || '',
      lastName: member.lastName || '',
      displayName: member.displayName || `${member.firstName} ${member.lastName}`,
      email: member.email || '',
      phoneNumber: member.phoneNumber || '',
      joinDate: member.joinDate || getTodayDate(),
      status: member.status || 'ACTIVE',
      bio: member.bio || '',
    });
    setIsEditModalOpen(true);
  };

  const handleUpdateMember = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedMember) return;
    setError(null);
    try {
      await memberApi.updateMember(selectedMember.id, {
        firstName: editForm.firstName,
        lastName: editForm.lastName,
        displayName: editForm.displayName,
        email: editForm.email,
        phoneNumber: editForm.phoneNumber || undefined,
        joinDate: editForm.joinDate,
        status: editForm.status,
        bio: editForm.bio || undefined,
      });
      setSuccessMsg('Member profile updated successfully!');
      setIsEditModalOpen(false);
      setSelectedMember(null);
      fetchData();
    } catch (err: any) {
      setError(err.message || 'Failed to update member');
    }
  };

  // Forms
  const [newMember, setNewMember] = useState({ firstName: '', lastName: '', email: '', joinDate: getTodayDate() });
  const [newRole, setNewRole] = useState({ name: '', description: '' });
  const [newRule, setNewRule] = useState({ code: '', name: '', points: 10 });
  const [newCert, setNewCert] = useState({ memberId: '', title: '', certificateType: 'ACHIEVEMENT' });

  const fetchData = async () => {
    setLoading(true);
    setError(null);
    try {
      const [defsRes, rulesRes, membersRes] = await Promise.all([
        roleApi.getRoleDefinitions().catch(() => []),
        pointApi.getPointRules().catch(() => []),
        memberApi.getMembers().catch(() => []),
      ]);
      setRoleDefs(defsRes);
      setPointRules(rulesRes);
      setMembers(membersRes);
    } catch (err: any) {
      setError(err.message || 'Failed to load admin data');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleCreateMember = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    try {
      await memberApi.createMember(newMember);
      setSuccessMsg('Member profile created successfully!');
      setIsMemberModalOpen(false);
      setNewMember({ firstName: '', lastName: '', email: '', joinDate: getTodayDate() });
      fetchData();
    } catch (err: any) {
      setError(err.message || 'Failed to create member');
    }
  };

  const handleCreateRole = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    try {
      await roleApi.createRoleDefinition(newRole);
      setSuccessMsg('Role definition created successfully!');
      setIsRoleModalOpen(false);
      setNewRole({ name: '', description: '' });
      fetchData();
    } catch (err: any) {
      setError(err.message || 'Failed to create role');
    }
  };

  const handleCreateRule = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    try {
      await pointApi.createPointRule(newRule);
      setSuccessMsg('Point rule created successfully!');
      setIsPointModalOpen(false);
      setNewRule({ code: '', name: '', points: 10 });
      fetchData();
    } catch (err: any) {
      setError(err.message || 'Failed to create point rule');
    }
  };

  const handleIssueCert = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    try {
      await achievementApi.issueCertificate(newCert.memberId, newCert.title, newCert.certificateType);
      setSuccessMsg('Certificate issued successfully!');
      setIsCertModalOpen(false);
      setNewCert({ memberId: '', title: '', certificateType: 'ACHIEVEMENT' });
    } catch (err: any) {
      setError(err.message || 'Failed to issue certificate');
    }
  };

  if (!isOfficer) {
    return (
      <AppLayout title="ExCom Administration">
        <ErrorMessage message="Access Restricted. Only Toastmasters Officers and Admins can access this area." />
      </AppLayout>
    );
  }

  return (
    <AppLayout title="ExCom Administration Panel">
      {error && <ErrorMessage message={error} />}
      {successMsg && (
        <div style={{ background: 'rgba(16, 185, 129, 0.15)', border: '1px solid rgba(16, 185, 129, 0.4)', color: '#34D399', padding: '12px 16px', borderRadius: 8, marginBottom: 20 }}>
          {successMsg}
        </div>
      )}

      {/* Quick Admin Actions */}
      <div style={{ display: 'flex', gap: 14, flexWrap: 'wrap', marginBottom: 28 }}>
        <Button variant="gold" onClick={() => setIsMemberModalOpen(true)}>
          <UserPlus size={16} /> Create Member Profile
        </Button>
        <Button variant="primary" onClick={() => setIsRoleModalOpen(true)}>
          <Plus size={16} /> Create Role Definition
        </Button>
        <Button variant="secondary" onClick={() => setIsPointModalOpen(true)}>
          <Award size={16} /> Create Point Rule
        </Button>
        <Button variant="secondary" onClick={() => setIsCertModalOpen(true)}>
          <FileCheck size={16} /> Issue Certificate
        </Button>
      </div>

      {loading ? (
        <LoadingSpinner />
      ) : (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))', gap: 24 }}>
          {/* Role Definitions Table */}
          <Card>
            <h3 style={{ fontSize: '1.1rem', marginBottom: 16 }}>Configured Role Definitions</h3>
            <div className="table-container">
              <table>
                <thead>
                  <tr>
                    <th>Role Name</th>
                    <th>Status</th>
                  </tr>
                </thead>
                <tbody>
                  {roleDefs.map((rd) => (
                    <tr key={rd.id}>
                      <td style={{ fontWeight: 700, color: '#F2DF74' }}>{rd.name}</td>
                      <td>
                        <Badge variant={rd.active ? 'completed' : 'cancelled'}>
                          {rd.active ? 'ACTIVE' : 'INACTIVE'}
                        </Badge>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </Card>

          {/* Point Rules Table */}
          <Card>
            <h3 style={{ fontSize: '1.1rem', marginBottom: 16 }}>Configured Point Rules</h3>
            <div className="table-container">
              <table>
                <thead>
                  <tr>
                    <th>Code</th>
                    <th>Rule Name</th>
                    <th>Points</th>
                  </tr>
                </thead>
                <tbody>
                  {pointRules.map((pr) => (
                    <tr key={pr.id}>
                      <td style={{ fontFamily: 'monospace', color: '#60A5FA' }}>{pr.code}</td>
                      <td style={{ fontWeight: 600 }}>{pr.name}</td>
                      <td style={{ fontWeight: 800, color: '#10B981' }}>+{pr.points} pts</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </Card>

          {/* Registered Club Members Table */}
          <Card style={{ gridColumn: '1 / -1' }}>
            <h3 style={{ fontSize: '1.1rem', marginBottom: 16 }}>Registered Club Members ({members.length})</h3>
            <div className="table-container">
              <table>
                <thead>
                  <tr>
                    <th>Member ID</th>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Join Date</th>
                    <th>Status</th>
                    <th>Account ID</th>
                    <th style={{ textAlign: 'right' }}>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {members.map((m) => (
                    <tr key={m.id}>
                      <td style={{ fontFamily: 'monospace', fontSize: '0.85rem', color: '#9CA3AF' }}>
                        {m.id}
                      </td>
                      <td style={{ fontWeight: 600, color: '#F2DF74' }}>
                        {m.displayName || `${m.firstName} ${m.lastName}`}
                      </td>
                      <td style={{ color: '#60A5FA' }}>{m.email}</td>
                      <td>{m.joinDate}</td>
                      <td>
                        <Badge variant={
                          m.status === 'ACTIVE' ? 'completed' :
                          m.status === 'INACTIVE' ? 'cancelled' :
                          m.status === 'SUSPENDED' ? 'cancelled' : 'scheduled'
                        }>
                          {m.status}
                        </Badge>
                      </td>
                      <td style={{ fontFamily: 'monospace', fontSize: '0.85rem', color: '#9CA3AF' }}>
                        {m.accountId ? m.accountId : 'N/A'}
                      </td>
                      <td style={{ textAlign: 'right' }}>
                        <div style={{ display: 'inline-flex', gap: 8 }}>
                          <Button variant="secondary" onClick={() => handleOpenViewModal(m)} style={{ padding: '4px 10px', fontSize: '0.8rem' }}>
                            <Eye size={14} /> View
                          </Button>
                          <Button variant="primary" onClick={() => handleOpenEditModal(m)} style={{ padding: '4px 10px', fontSize: '0.8rem' }}>
                            <Edit size={14} /> Edit
                          </Button>
                        </div>
                      </td>
                    </tr>
                  ))}
                  {members.length === 0 && (
                    <tr>
                      <td colSpan={7} style={{ textAlign: 'center', color: '#9CA3AF', padding: 20 }}>
                        No members found. Use "Create Member Profile" above to add one.
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </Card>
        </div>
      )}

      {/* Create Member Modal */}
      <Modal isOpen={isMemberModalOpen} onClose={() => setIsMemberModalOpen(false)} title="Create New Member Profile">
        <form onSubmit={handleCreateMember}>
          <div className="form-group">
            <label className="form-label">First Name</label>
            <input
              type="text"
              required
              className="form-input"
              value={newMember.firstName}
              onChange={(e) => setNewMember({ ...newMember, firstName: e.target.value })}
            />
          </div>
          <div className="form-group">
            <label className="form-label">Last Name</label>
            <input
              type="text"
              required
              className="form-input"
              value={newMember.lastName}
              onChange={(e) => setNewMember({ ...newMember, lastName: e.target.value })}
            />
          </div>
          <div className="form-group">
            <label className="form-label">Email Address</label>
            <input
              type="email"
              required
              className="form-input"
              value={newMember.email}
              onChange={(e) => setNewMember({ ...newMember, email: e.target.value })}
            />
          </div>
          <div className="form-group">
            <label className="form-label">Join Date</label>
            <input
              type="date"
              required
              className="form-input"
              value={newMember.joinDate}
              onChange={(e) => setNewMember({ ...newMember, joinDate: e.target.value })}
            />
          </div>
          <Button type="submit" variant="gold" style={{ width: '100%', marginTop: 10 }}>
            Create Member
          </Button>
        </form>
      </Modal>

      {/* Create Role Modal */}
      <Modal isOpen={isRoleModalOpen} onClose={() => setIsRoleModalOpen(false)} title="Create Custom Role Definition">
        <form onSubmit={handleCreateRole}>
          <div className="form-group">
            <label className="form-label">Role Name</label>
            <input
              type="text"
              required
              className="form-input"
              placeholder="e.g. Humorous Speaker"
              value={newRole.name}
              onChange={(e) => setNewRole({ ...newRole, name: e.target.value })}
            />
          </div>
          <div className="form-group">
            <label className="form-label">Description</label>
            <input
              type="text"
              className="form-input"
              value={newRole.description}
              onChange={(e) => setNewRole({ ...newRole, description: e.target.value })}
            />
          </div>
          <Button type="submit" variant="primary" style={{ width: '100%', marginTop: 10 }}>
            Save Role Definition
          </Button>
        </form>
      </Modal>

      {/* Create Point Rule Modal */}
      <Modal isOpen={isPointModalOpen} onClose={() => setIsPointModalOpen(false)} title="Create Point Rule">
        <form onSubmit={handleCreateRule}>
          <div className="form-group">
            <label className="form-label">Rule Code</label>
            <input
              type="text"
              required
              className="form-input"
              placeholder="e.g. ROLE_TABLE_TOPICS"
              value={newRule.code}
              onChange={(e) => setNewRule({ ...newRule, code: e.target.value })}
            />
          </div>
          <div className="form-group">
            <label className="form-label">Rule Name</label>
            <input
              type="text"
              required
              className="form-input"
              placeholder="e.g. Table Topics Speaker"
              value={newRule.name}
              onChange={(e) => setNewRule({ ...newRule, name: e.target.value })}
            />
          </div>
          <div className="form-group">
            <label className="form-label">Points Awarded</label>
            <input
              type="number"
              required
              className="form-input"
              value={newRule.points}
              onChange={(e) => setNewRule({ ...newRule, points: parseInt(e.target.value) })}
            />
          </div>
          <Button type="submit" variant="gold" style={{ width: '100%', marginTop: 10 }}>
            Create Point Rule
          </Button>
        </form>
      </Modal>

      {/* Issue Certificate Modal */}
      <Modal isOpen={isCertModalOpen} onClose={() => setIsCertModalOpen(false)} title="Issue Official Certificate">
        <form onSubmit={handleIssueCert}>
          <div className="form-group">
            <label className="form-label">Member UUID</label>
            <input
              type="text"
              required
              className="form-input"
              placeholder="Member UUID"
              value={newCert.memberId}
              onChange={(e) => setNewCert({ ...newCert, memberId: e.target.value })}
            />
          </div>
          <div className="form-group">
            <label className="form-label">Certificate Title</label>
            <input
              type="text"
              required
              className="form-input"
              placeholder="e.g. Best Speaker Award"
              value={newCert.title}
              onChange={(e) => setNewCert({ ...newCert, title: e.target.value })}
            />
          </div>
          <Button type="submit" variant="gold" style={{ width: '100%', marginTop: 10 }}>
            Issue Certificate
          </Button>
        </form>
      </Modal>

      {/* View Member Modal */}
      <Modal isOpen={isViewModalOpen} onClose={() => setIsViewModalOpen(false)} title="Member Profile Details">
        {selectedMember && (
          <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12 }}>
              <div>
                <span style={{ fontSize: '0.8rem', color: '#9CA3AF', display: 'block' }}>First Name</span>
                <strong style={{ fontSize: '0.95rem' }}>{selectedMember.firstName}</strong>
              </div>
              <div>
                <span style={{ fontSize: '0.8rem', color: '#9CA3AF', display: 'block' }}>Last Name</span>
                <strong style={{ fontSize: '0.95rem' }}>{selectedMember.lastName}</strong>
              </div>
              <div>
                <span style={{ fontSize: '0.8rem', color: '#9CA3AF', display: 'block' }}>Display Name</span>
                <strong style={{ fontSize: '0.95rem', color: '#F2DF74' }}>{selectedMember.displayName || `${selectedMember.firstName} ${selectedMember.lastName}`}</strong>
              </div>
              <div>
                <span style={{ fontSize: '0.8rem', color: '#9CA3AF', display: 'block' }}>Status</span>
                <Badge variant={
                  selectedMember.status === 'ACTIVE' ? 'completed' :
                  selectedMember.status === 'INACTIVE' ? 'cancelled' :
                  selectedMember.status === 'SUSPENDED' ? 'cancelled' : 'scheduled'
                }>
                  {selectedMember.status}
                </Badge>
              </div>
              <div>
                <span style={{ fontSize: '0.8rem', color: '#9CA3AF', display: 'block' }}>Email Address</span>
                <span style={{ color: '#60A5FA' }}>{selectedMember.email}</span>
              </div>
              <div>
                <span style={{ fontSize: '0.8rem', color: '#9CA3AF', display: 'block' }}>Phone Number</span>
                <span>{selectedMember.phoneNumber || 'Not provided'}</span>
              </div>
              <div>
                <span style={{ fontSize: '0.8rem', color: '#9CA3AF', display: 'block' }}>Join Date</span>
                <span>{selectedMember.joinDate}</span>
              </div>
              <div>
                <span style={{ fontSize: '0.8rem', color: '#9CA3AF', display: 'block' }}>Account ID</span>
                <span style={{ fontFamily: 'monospace', fontSize: '0.85rem' }}>{selectedMember.accountId || 'N/A (No User Account Linked)'}</span>
              </div>
            </div>

            <div style={{ borderTop: '1px solid rgba(255,255,255,0.1)', paddingTop: 10 }}>
              <span style={{ fontSize: '0.8rem', color: '#9CA3AF', display: 'block' }}>Member UUID</span>
              <span style={{ fontFamily: 'monospace', fontSize: '0.85rem', color: '#9CA3AF' }}>{selectedMember.id}</span>
            </div>

            {selectedMember.bio && (
              <div>
                <span style={{ fontSize: '0.8rem', color: '#9CA3AF', display: 'block' }}>Bio</span>
                <p style={{ fontSize: '0.9rem', color: '#D1D5DB', marginTop: 4 }}>{selectedMember.bio}</p>
              </div>
            )}

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12, borderTop: '1px solid rgba(255,255,255,0.1)', paddingTop: 10 }}>
              <div>
                <span style={{ fontSize: '0.75rem', color: '#6B7280', display: 'block' }}>Created Date</span>
                <span style={{ fontSize: '0.8rem', color: '#9CA3AF' }}>{selectedMember.createdAt ? new Date(selectedMember.createdAt).toLocaleString() : 'N/A'}</span>
              </div>
              <div>
                <span style={{ fontSize: '0.75rem', color: '#6B7280', display: 'block' }}>Last Updated</span>
                <span style={{ fontSize: '0.8rem', color: '#9CA3AF' }}>{selectedMember.updatedAt ? new Date(selectedMember.updatedAt).toLocaleString() : 'N/A'}</span>
              </div>
            </div>

            <div style={{ display: 'flex', gap: 10, marginTop: 10 }}>
              <Button variant="primary" style={{ flex: 1 }} onClick={() => { setIsViewModalOpen(false); handleOpenEditModal(selectedMember); }}>
                <Edit size={14} /> Edit Member
              </Button>
              <Button variant="secondary" onClick={() => setIsViewModalOpen(false)}>
                Close
              </Button>
            </div>
          </div>
        )}
      </Modal>

      {/* Edit Member Modal */}
      <Modal isOpen={isEditModalOpen} onClose={() => setIsEditModalOpen(false)} title="Edit Member Profile & Status">
        <form onSubmit={handleUpdateMember}>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12 }}>
            <div className="form-group">
              <label className="form-label">First Name</label>
              <input
                type="text"
                required
                className="form-input"
                value={editForm.firstName}
                onChange={(e) => setEditForm({ ...editForm, firstName: e.target.value })}
              />
            </div>
            <div className="form-group">
              <label className="form-label">Last Name</label>
              <input
                type="text"
                required
                className="form-input"
                value={editForm.lastName}
                onChange={(e) => setEditForm({ ...editForm, lastName: e.target.value })}
              />
            </div>
          </div>

          <div className="form-group">
            <label className="form-label">Display Name</label>
            <input
              type="text"
              className="form-input"
              value={editForm.displayName}
              onChange={(e) => setEditForm({ ...editForm, displayName: e.target.value })}
            />
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12 }}>
            <div className="form-group">
              <label className="form-label">Email Address</label>
              <input
                type="email"
                required
                className="form-input"
                value={editForm.email}
                onChange={(e) => setEditForm({ ...editForm, email: e.target.value })}
              />
            </div>
            <div className="form-group">
              <label className="form-label">Phone Number</label>
              <input
                type="text"
                className="form-input"
                placeholder="e.g. +1 555-0192"
                value={editForm.phoneNumber}
                onChange={(e) => setEditForm({ ...editForm, phoneNumber: e.target.value })}
              />
            </div>
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12 }}>
            <div className="form-group">
              <label className="form-label">Join Date</label>
              <input
                type="date"
                required
                className="form-input"
                value={editForm.joinDate}
                onChange={(e) => setEditForm({ ...editForm, joinDate: e.target.value })}
              />
            </div>
            <div className="form-group">
              <label className="form-label">Member Status</label>
              <select
                className="form-input"
                style={{ background: '#1E293B', color: '#F8FAFC' }}
                value={editForm.status}
                onChange={(e) => setEditForm({ ...editForm, status: e.target.value })}
              >
                <option value="ACTIVE">ACTIVE</option>
                <option value="INACTIVE">INACTIVE</option>
                <option value="SUSPENDED">SUSPENDED</option>
                <option value="ALUMNI">ALUMNI</option>
              </select>
            </div>
          </div>

          <div className="form-group">
            <label className="form-label">Bio / Profile Description</label>
            <textarea
              className="form-input"
              rows={3}
              style={{ resize: 'vertical' }}
              value={editForm.bio}
              onChange={(e) => setEditForm({ ...editForm, bio: e.target.value })}
            />
          </div>

          <div style={{ display: 'flex', gap: 10, marginTop: 16 }}>
            <Button type="submit" variant="gold" style={{ flex: 1 }}>
              Save Changes
            </Button>
            <Button type="button" variant="secondary" onClick={() => setIsEditModalOpen(false)}>
              Cancel
            </Button>
          </div>
        </form>
      </Modal>
    </AppLayout>
  );
};
