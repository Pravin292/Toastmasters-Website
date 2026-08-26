import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { AppLayout } from '../components/layout/AppLayout';
import { Card } from '../components/common/Card';
import { Badge } from '../components/common/Badge';
import { Button } from '../components/common/Button';
import { Modal } from '../components/common/Modal';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { ErrorMessage } from '../components/common/ErrorMessage';
import { useAuth } from '../context/AuthContext';
import { meetingApi } from '../api/meetingApi';
import { roleApi } from '../api/roleApi';
import { attendanceApi } from '../api/attendanceApi';
import { aiApi } from '../api/aiApi';
import { memberApi } from '../api/memberApi';
import { MeetingWorkflow, RoleAssignment, RoleDefinition, Attendance, AiSummary, Member, Meeting, MeetingStatus, MeetingType } from '../types';
import { Play, CheckCircle, AlertTriangle, Sparkles, UserPlus, Check, X, Clock, Users, Edit } from 'lucide-react';

export const MeetingDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const { isOfficer } = useAuth();

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [workflow, setWorkflow] = useState<MeetingWorkflow | null>(null);
  const [roleAssignments, setRoleAssignments] = useState<RoleAssignment[]>([]);
  const [roleDefs, setRoleDefs] = useState<RoleDefinition[]>([]);
  const [attendanceList, setAttendanceList] = useState<Attendance[]>([]);
  const [members, setMembers] = useState<Member[]>([]);
  const [aiSummary, setAiSummary] = useState<AiSummary | null>(null);
  const [aiLoading, setAiLoading] = useState(false);
  const [aiError, setAiError] = useState<string | null>(null);

  // Role Assignment Modal State
  const [isRoleModalOpen, setIsRoleModalOpen] = useState(false);
  const [selectedRoleDefId, setSelectedRoleDefId] = useState('');
  const [assigneeMemberId, setAssigneeMemberId] = useState('');
  const [assigning, setAssigning] = useState(false);
  const [roleErrorMsg, setRoleErrorMsg] = useState<string | null>(null);

  // Attendance Roster State
  const [updatingAttendanceMemberId, setUpdatingAttendanceMemberId] = useState<string | null>(null);
  const [attendanceSuccessMsg, setAttendanceSuccessMsg] = useState<string | null>(null);
  const [attendanceErrorMsg, setAttendanceErrorMsg] = useState<string | null>(null);

  // Edit Meeting State
  const [meetingDetails, setMeetingDetails] = useState<Meeting | null>(null);
  const [isEditMeetingModalOpen, setIsEditMeetingModalOpen] = useState(false);
  const [editMeetingForm, setEditMeetingForm] = useState({
    meetingNumber: 101,
    meetingStart: '',
    theme: '',
    meetingType: 'REGULAR' as MeetingType,
    status: 'SCHEDULED' as MeetingStatus,
    location: '',
    meetingUrl: '',
    description: '',
  });
  const [updatingMeeting, setUpdatingMeeting] = useState(false);
  const [editMeetingSuccessMsg, setEditMeetingSuccessMsg] = useState<string | null>(null);
  const [editMeetingErrorMsg, setEditMeetingErrorMsg] = useState<string | null>(null);

  const fetchMeetingData = async () => {
    if (!id) return;
    setLoading(true);
    setError(null);
    try {
      const [wfRes, rolesRes, defsRes, attRes, membersRes, meetingRes] = await Promise.all([
        meetingApi.getMeetingWorkflow(id),
        roleApi.getMeetingRoleAssignments(id).catch(() => []),
        roleApi.getRoleDefinitions().catch(() => []),
        attendanceApi.getMeetingAttendance(id).catch(() => []),
        memberApi.getMembers().catch(() => []),
        meetingApi.getMeetingById(id).catch(() => null),
      ]);

      setWorkflow(wfRes);
      setRoleAssignments(rolesRes);
      setRoleDefs(defsRes);
      setAttendanceList(attRes);
      setMembers(membersRes);
      if (meetingRes) {
        setMeetingDetails(meetingRes);
      }
    } catch (err: any) {
      setError(err.message || 'Failed to load meeting workflow');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchMeetingData();
  }, [id]);

  const handleStartMeeting = async () => {
    if (!id) return;
    try {
      await meetingApi.startMeeting(id);
      fetchMeetingData();
    } catch (err: any) {
      setError(err.message || 'Failed to start meeting');
    }
  };

  const handleCompleteMeeting = async () => {
    if (!id) return;
    try {
      await meetingApi.completeMeeting(id);
      fetchMeetingData();
    } catch (err: any) {
      setError(err.message || 'Failed to complete meeting');
    }
  };

  const handleOpenRoleModal = () => {
    setSelectedRoleDefId('');
    setAssigneeMemberId('');
    setRoleErrorMsg(null);
    setIsRoleModalOpen(true);
  };

  const handleAssignRole = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!id || !selectedRoleDefId || !assigneeMemberId) return;
    setAssigning(true);
    setRoleErrorMsg(null);
    try {
      await roleApi.assignRole(id, selectedRoleDefId, assigneeMemberId);
      setIsRoleModalOpen(false);
      setSelectedRoleDefId('');
      setAssigneeMemberId('');
      fetchMeetingData();
    } catch (err: any) {
      setRoleErrorMsg(err.message || 'Failed to assign role');
    } finally {
      setAssigning(false);
    }
  };

  const handleOpenEditMeetingModal = () => {
    if (!meetingDetails) return;
    setEditMeetingForm({
      meetingNumber: meetingDetails.meetingNumber,
      meetingStart: meetingDetails.meetingStart ? new Date(meetingDetails.meetingStart).toISOString().slice(0, 16) : '',
      theme: meetingDetails.theme || '',
      meetingType: meetingDetails.meetingType || 'REGULAR',
      status: meetingDetails.status || 'SCHEDULED',
      location: meetingDetails.location || '',
      meetingUrl: meetingDetails.meetingUrl || '',
      description: meetingDetails.description || '',
    });
    setEditMeetingSuccessMsg(null);
    setEditMeetingErrorMsg(null);
    setIsEditMeetingModalOpen(true);
  };

  const handleUpdateMeetingDetails = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!id) return;
    setUpdatingMeeting(true);
    setEditMeetingSuccessMsg(null);
    setEditMeetingErrorMsg(null);
    try {
      const formattedStart = new Date(editMeetingForm.meetingStart).toISOString();
      await meetingApi.updateMeeting(id, {
        meetingNumber: editMeetingForm.meetingNumber,
        meetingStart: formattedStart,
        theme: editMeetingForm.theme,
        meetingType: editMeetingForm.meetingType,
        status: editMeetingForm.status,
        location: editMeetingForm.location || undefined,
        meetingUrl: editMeetingForm.meetingUrl || undefined,
        description: editMeetingForm.description || undefined,
      });
      setEditMeetingSuccessMsg('Meeting details updated successfully!');
      setIsEditMeetingModalOpen(false);
      await fetchMeetingData();
    } catch (err: any) {
      setEditMeetingErrorMsg(err.message || 'Failed to update meeting details');
    } finally {
      setUpdatingMeeting(false);
    }
  };

  const handleUpdateAttendanceStatus = async (memberId: string, memberName: string, newStatus: 'PRESENT' | 'ABSENT' | 'EXCUSED') => {
    if (!id) return;
    setUpdatingAttendanceMemberId(memberId);
    setAttendanceSuccessMsg(null);
    setAttendanceErrorMsg(null);
    try {
      const existingRecord = attendanceList.find((a) => a.memberId === memberId);
      if (existingRecord) {
        await attendanceApi.updateAttendance(existingRecord.id, newStatus);
      } else {
        await attendanceApi.recordAttendance(id, memberId, newStatus);
      }
      setAttendanceSuccessMsg(`Attendance marked as ${newStatus} for ${memberName}`);
      await fetchMeetingData();
    } catch (err: any) {
      setAttendanceErrorMsg(err.message || 'Failed to update attendance');
    } finally {
      setUpdatingAttendanceMemberId(null);
    }
  };

  const handleRecordAttendance = async (memberId: string, status: 'PRESENT' | 'ABSENT' | 'EXCUSED') => {
    if (!id) return;
    try {
      await attendanceApi.recordAttendance(id, memberId, status);
      fetchMeetingData();
    } catch (err: any) {
      setError(err.message || 'Failed to record attendance');
    }
  };

  const handleGenerateAiSummary = async () => {
    if (!id) return;
    setAiLoading(true);
    setAiError(null);
    try {
      const summaryRes = await aiApi.generateMeetingSummary(id);
      setAiSummary(summaryRes);
    } catch (err: any) {
      setAiError(err.message || 'AI Summary unavailable');
    } finally {
      setAiLoading(false);
    }
  };

  if (loading) {
    return (
      <AppLayout title="Meeting Details & Workflow">
        <LoadingSpinner />
      </AppLayout>
    );
  }

  if (!workflow) {
    return (
      <AppLayout title="Meeting Details & Workflow">
        <ErrorMessage message="Meeting details not found." />
      </AppLayout>
    );
  }

  return (
    <AppLayout title={`Meeting #${workflow.meetingNumber} Workflow`}>
      {error && <ErrorMessage message={error} />}

      {/* Header Banner */}
      <div style={{
        background: 'var(--bg-card)',
        border: '1px solid var(--bg-card-border)',
        borderRadius: 18,
        padding: 24,
        marginBottom: 24,
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        flexWrap: 'wrap',
        gap: 16
      }}>
        <div>
          <div style={{ display: 'flex', alignItems: 'center', gap: 12, marginBottom: 8 }}>
            <span style={{ fontSize: '1.4rem', fontWeight: 800, color: '#F2DF74' }}>
              Meeting #{workflow.meetingNumber}
            </span>
            <Badge variant={workflow.status.toLowerCase() as any}>
              {workflow.status}
            </Badge>
          </div>
          <h2 style={{ fontSize: '1.2rem', color: '#94A3B8' }}>
            Theme: {workflow.theme || 'Regular Meeting'}
          </h2>
        </div>

        {/* Workflow Actions */}
        {isOfficer && (
          <div style={{ display: 'flex', gap: 12 }}>
            <Button variant="secondary" onClick={handleOpenEditMeetingModal}>
              <Edit size={16} /> Edit Meeting
            </Button>
            {workflow.canStart && (
              <Button variant="gold" onClick={handleStartMeeting}>
                <Play size={16} /> Start Meeting
              </Button>
            )}
            {workflow.canComplete && (
              <Button variant="primary" onClick={handleCompleteMeeting}>
                <CheckCircle size={16} /> Complete Meeting
              </Button>
            )}
          </div>
        )}
      </div>

      {/* Workflow Warnings */}
      {workflow.workflowWarnings && workflow.workflowWarnings.length > 0 && (
        <div style={{
          background: 'rgba(245, 158, 11, 0.12)',
          border: '1px solid rgba(245, 158, 11, 0.3)',
          color: '#FBBF24',
          padding: '14px 18px',
          borderRadius: 12,
          marginBottom: 24
        }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 8, fontWeight: 700, marginBottom: 6 }}>
            <AlertTriangle size={18} />
            <span>Meeting Completeness Warnings</span>
          </div>
          <ul style={{ paddingLeft: 24, fontSize: '0.9rem' }}>
            {workflow.workflowWarnings.map((w, idx) => (
              <li key={idx}>{w}</li>
            ))}
          </ul>
        </div>
      )}

      {/* Metrics Row */}
      <div className="card-grid" style={{ marginBottom: 28 }}>
        <Card>
          <div className="card-title">Attendance Coverage</div>
          <div className="card-value" style={{ color: '#60A5FA' }}>
            {Math.round(workflow.attendanceSummary.attendancePercentage)}%
          </div>
          <div style={{ fontSize: '0.8rem', color: '#64748B' }}>
            {workflow.attendanceSummary.presentCount} Present / {workflow.attendanceSummary.totalRecords} Recorded
          </div>
        </Card>

        <Card>
          <div className="card-title">Roles Coverage</div>
          <div className="card-value" style={{ color: '#F2DF74' }}>
            {workflow.roleSummary.rolesAssigned} Filled
          </div>
          <div style={{ fontSize: '0.8rem', color: '#64748B' }}>
            {workflow.roleSummary.rolesRemaining} Roles Unassigned
          </div>
        </Card>

        <Card>
          <div className="card-title">Total Points Awarded</div>
          <div className="card-value" style={{ color: '#10B981' }}>
            {workflow.pointsSummary.totalPointsAwarded} pts
          </div>
          <div style={{ fontSize: '0.8rem', color: '#64748B' }}>
            Awarded for Meeting Roles
          </div>
        </Card>
      </div>

      {/* Role Assignments Section */}
      <Card style={{ marginBottom: 28 }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 18 }}>
          <h3 style={{ fontSize: '1.1rem' }}>Meeting Role Assignments</h3>
          {isOfficer && (
            <Button variant="secondary" style={{ padding: '6px 14px', fontSize: '0.85rem' }} onClick={handleOpenRoleModal}>
              <UserPlus size={16} /> Assign Role
            </Button>
          )}
        </div>

        <div className="table-container">
          <table>
            <thead>
              <tr>
                <th>Role Name</th>
                <th>Assigned Member</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {roleAssignments.map((ra) => (
                <tr key={ra.id}>
                  <td style={{ fontWeight: 700, color: '#F2DF74' }}>{ra.roleName}</td>
                  <td>{ra.memberDisplayName}</td>
                  <td>
                    <Badge variant="completed">ASSIGNED</Badge>
                  </td>
                </tr>
              ))}
              {roleAssignments.length === 0 && (
                <tr>
                  <td colSpan={3} style={{ textAlign: 'center', color: '#64748B', padding: 24 }}>
                    No roles assigned for this meeting yet.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </Card>

      {/* Interactive Attendance Roster Section */}
      <Card style={{ marginBottom: 28 }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 18, flexWrap: 'wrap', gap: 12 }}>
          <div>
            <h3 style={{ fontSize: '1.1rem', display: 'flex', alignItems: 'center', gap: 8 }}>
              <Users size={20} color="#60A5FA" />
              <span>Interactive Attendance Roster</span>
            </h3>
            <p style={{ fontSize: '0.85rem', color: '#94A3B8' }}>
              Mark members present, absent, or excused. Marking PRESENT automatically awards +5 attendance points.
            </p>
          </div>
          <div style={{ display: 'flex', gap: 8, fontSize: '0.82rem', color: '#94A3B8' }}>
            <span style={{ display: 'inline-flex', alignItems: 'center', gap: 4 }}>
              <Badge variant="completed">PRESENT: {attendanceList.filter(a => a.status === 'PRESENT').length}</Badge>
            </span>
            <span style={{ display: 'inline-flex', alignItems: 'center', gap: 4 }}>
              <Badge variant="cancelled">ABSENT: {attendanceList.filter(a => a.status === 'ABSENT').length}</Badge>
            </span>
            <span style={{ display: 'inline-flex', alignItems: 'center', gap: 4 }}>
              <Badge variant="scheduled">EXCUSED: {attendanceList.filter(a => a.status === 'EXCUSED').length}</Badge>
            </span>
          </div>
        </div>

        {attendanceSuccessMsg && (
          <div style={{ background: 'rgba(16, 185, 129, 0.15)', border: '1px solid rgba(16, 185, 129, 0.4)', color: '#34D399', padding: '10px 14px', borderRadius: 8, marginBottom: 16, fontSize: '0.9rem' }}>
            {attendanceSuccessMsg}
          </div>
        )}

        {attendanceErrorMsg && (
          <div style={{ marginBottom: 16 }}>
            <ErrorMessage message={attendanceErrorMsg} />
          </div>
        )}

        <div className="table-container">
          <table>
            <thead>
              <tr>
                <th>Member Name</th>
                <th>Email</th>
                <th>Current Status</th>
                <th style={{ textAlign: 'right' }}>Mark Attendance</th>
              </tr>
            </thead>
            <tbody>
              {members
                .filter((m) => m.status === 'ACTIVE')
                .map((m) => {
                  const att = attendanceList.find((a) => a.memberId === m.id);
                  const currentStatus = att ? att.status : 'UNRECORDED';
                  const isUpdating = updatingAttendanceMemberId === m.id;
                  const memberDisplayName = m.displayName || `${m.firstName} ${m.lastName}`;

                  return (
                    <tr key={m.id}>
                      <td style={{ fontWeight: 600, color: '#F2DF74' }}>
                        {memberDisplayName}
                      </td>
                      <td style={{ color: '#60A5FA' }}>{m.email}</td>
                      <td>
                        <Badge variant={
                          currentStatus === 'PRESENT' ? 'completed' :
                          currentStatus === 'ABSENT' ? 'cancelled' :
                          currentStatus === 'EXCUSED' ? 'scheduled' : 'default'
                        }>
                          {currentStatus}
                        </Badge>
                      </td>
                      <td style={{ textAlign: 'right' }}>
                        <div style={{ display: 'inline-flex', gap: 6 }}>
                          <button
                            type="button"
                            disabled={isUpdating || !isOfficer}
                            onClick={() => handleUpdateAttendanceStatus(m.id, memberDisplayName, 'PRESENT')}
                            style={{
                              padding: '5px 12px',
                              borderRadius: 6,
                              border: currentStatus === 'PRESENT' ? '1px solid #10B981' : '1px solid rgba(255,255,255,0.1)',
                              background: currentStatus === 'PRESENT' ? 'rgba(16, 185, 129, 0.25)' : 'rgba(255,255,255,0.05)',
                              color: currentStatus === 'PRESENT' ? '#34D399' : '#94A3B8',
                              fontWeight: currentStatus === 'PRESENT' ? 700 : 500,
                              fontSize: '0.8rem',
                              cursor: isUpdating || !isOfficer ? 'not-allowed' : 'pointer',
                              opacity: isUpdating ? 0.6 : 1,
                              transition: 'all 0.2s'
                            }}
                          >
                            {isUpdating && currentStatus !== 'PRESENT' ? '...' : 'Present'}
                          </button>

                          <button
                            type="button"
                            disabled={isUpdating || !isOfficer}
                            onClick={() => handleUpdateAttendanceStatus(m.id, memberDisplayName, 'ABSENT')}
                            style={{
                              padding: '5px 12px',
                              borderRadius: 6,
                              border: currentStatus === 'ABSENT' ? '1px solid #EF4444' : '1px solid rgba(255,255,255,0.1)',
                              background: currentStatus === 'ABSENT' ? 'rgba(239, 68, 68, 0.25)' : 'rgba(255,255,255,0.05)',
                              color: currentStatus === 'ABSENT' ? '#F87171' : '#94A3B8',
                              fontWeight: currentStatus === 'ABSENT' ? 700 : 500,
                              fontSize: '0.8rem',
                              cursor: isUpdating || !isOfficer ? 'not-allowed' : 'pointer',
                              opacity: isUpdating ? 0.6 : 1,
                              transition: 'all 0.2s'
                            }}
                          >
                            {isUpdating && currentStatus !== 'ABSENT' ? '...' : 'Absent'}
                          </button>

                          <button
                            type="button"
                            disabled={isUpdating || !isOfficer}
                            onClick={() => handleUpdateAttendanceStatus(m.id, memberDisplayName, 'EXCUSED')}
                            style={{
                              padding: '5px 12px',
                              borderRadius: 6,
                              border: currentStatus === 'EXCUSED' ? '1px solid #F59E0B' : '1px solid rgba(255,255,255,0.1)',
                              background: currentStatus === 'EXCUSED' ? 'rgba(245, 158, 11, 0.25)' : 'rgba(255,255,255,0.05)',
                              color: currentStatus === 'EXCUSED' ? '#FBBF24' : '#94A3B8',
                              fontWeight: currentStatus === 'EXCUSED' ? 700 : 500,
                              fontSize: '0.8rem',
                              cursor: isUpdating || !isOfficer ? 'not-allowed' : 'pointer',
                              opacity: isUpdating ? 0.6 : 1,
                              transition: 'all 0.2s'
                            }}
                          >
                            {isUpdating && currentStatus !== 'EXCUSED' ? '...' : 'Excused'}
                          </button>
                        </div>
                      </td>
                    </tr>
                  );
                })}

              {members.filter((m) => m.status === 'ACTIVE').length === 0 && (
                <tr>
                  <td colSpan={4} style={{ textAlign: 'center', color: '#64748B', padding: 24 }}>
                    No active members found in the club roster.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </Card>

      {/* AI Meeting Summary & Insights Section */}
      <Card>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 18, flexWrap: 'wrap', gap: 12 }}>
          <div>
            <h3 style={{ fontSize: '1.1rem', display: 'flex', alignItems: 'center', gap: 8 }}>
              <Sparkles size={20} color="#F2DF74" />
              <span>AI Meeting Summary & Insights</span>
            </h3>
            <p style={{ fontSize: '0.85rem', color: '#94A3B8' }}>
              Generates executive summary and participation analysis from meeting backend data.
            </p>
          </div>

          <Button variant="gold" onClick={handleGenerateAiSummary} disabled={aiLoading}>
            {aiLoading ? 'Generating AI Insights...' : 'Generate AI Summary'}
          </Button>
        </div>

        {aiError && <ErrorMessage message={aiError} />}

        {aiLoading && <LoadingSpinner />}

        {aiSummary && (
          <div style={{ display: 'flex', flexDirection: 'column', gap: 16, marginTop: 16 }}>
            <div style={{ background: 'rgba(255,255,255,0.03)', padding: 18, borderRadius: 12, border: '1px solid rgba(255,255,255,0.06)' }}>
              <h4 style={{ color: '#F2DF74', marginBottom: 8, fontSize: '0.95rem' }}>Executive Summary</h4>
              <p style={{ fontSize: '0.95rem', lineHeight: 1.6 }}>{aiSummary.summary}</p>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))', gap: 16 }}>
              <div style={{ background: 'rgba(59, 130, 246, 0.08)', padding: 16, borderRadius: 12, border: '1px solid rgba(59, 130, 246, 0.2)' }}>
                <h4 style={{ color: '#60A5FA', marginBottom: 6, fontSize: '0.9rem' }}>Attendance Insights</h4>
                <p style={{ fontSize: '0.88rem', color: '#CBD5E1' }}>{aiSummary.attendanceInsights}</p>
              </div>

              <div style={{ background: 'rgba(16, 185, 129, 0.08)', padding: 16, borderRadius: 12, border: '1px solid rgba(16, 185, 129, 0.2)' }}>
                <h4 style={{ color: '#34D399', marginBottom: 6, fontSize: '0.9rem' }}>Role Participation</h4>
                <p style={{ fontSize: '0.88rem', color: '#CBD5E1' }}>{aiSummary.roleInsights}</p>
              </div>

              <div style={{ background: 'rgba(245, 158, 11, 0.08)', padding: 16, borderRadius: 12, border: '1px solid rgba(245, 158, 11, 0.2)' }}>
                <h4 style={{ color: '#FBBF24', marginBottom: 6, fontSize: '0.9rem' }}>Performance Analysis</h4>
                <p style={{ fontSize: '0.88rem', color: '#CBD5E1' }}>{aiSummary.performanceInsights}</p>
              </div>
            </div>
          </div>
        )}
      </Card>

      {/* Role Assignment Modal */}
      <Modal isOpen={isRoleModalOpen} onClose={() => setIsRoleModalOpen(false)} title="Assign Meeting Role">
        {roleErrorMsg && <ErrorMessage message={roleErrorMsg} />}
        <form onSubmit={handleAssignRole}>
          <div className="form-group">
            <label className="form-label">Select Role Definition</label>
            <select
              className="form-select"
              required
              value={selectedRoleDefId}
              onChange={(e) => setSelectedRoleDefId(e.target.value)}
            >
              <option value="">-- Choose Role --</option>
              {roleDefs.map((def) => (
                <option key={def.id} value={def.id}>{def.name}</option>
              ))}
            </select>
          </div>

          <div className="form-group">
            <label className="form-label">Assign Active Member</label>
            <select
              className="form-select"
              required
              value={assigneeMemberId}
              onChange={(e) => setAssigneeMemberId(e.target.value)}
            >
              <option value="">-- Select a Member --</option>
              {members
                .filter((m) => m.status === 'ACTIVE')
                .map((m) => (
                  <option key={m.id} value={m.id}>
                    {m.displayName || `${m.firstName} ${m.lastName}`} ({m.email})
                  </option>
                ))}
            </select>
            {members.filter((m) => m.status === 'ACTIVE').length === 0 && (
              <p style={{ fontSize: '0.8rem', color: '#EF4444', marginTop: 6 }}>
                No active members available. Please create or activate a member profile first.
              </p>
            )}
          </div>

          <Button
            type="submit"
            variant="gold"
            style={{ width: '100%', marginTop: 10 }}
            disabled={assigning || !selectedRoleDefId || !assigneeMemberId}
          >
            {assigning ? 'Assigning...' : 'Assign Role'}
          </Button>
        </form>
      </Modal>

      {/* Edit Meeting Modal */}
      <Modal isOpen={isEditMeetingModalOpen} onClose={() => setIsEditMeetingModalOpen(false)} title="Edit Meeting Details">
        {editMeetingErrorMsg && <ErrorMessage message={editMeetingErrorMsg} />}
        <form onSubmit={handleUpdateMeetingDetails}>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12 }}>
            <div className="form-group">
              <label className="form-label">Meeting Number</label>
              <input
                type="number"
                required
                min={1}
                className="form-input"
                value={editMeetingForm.meetingNumber}
                onChange={(e) => setEditMeetingForm({ ...editMeetingForm, meetingNumber: parseInt(e.target.value) || 1 })}
              />
            </div>
            <div className="form-group">
              <label className="form-label">Meeting Start Date & Time</label>
              <input
                type="datetime-local"
                required
                className="form-input"
                value={editMeetingForm.meetingStart}
                onChange={(e) => setEditMeetingForm({ ...editMeetingForm, meetingStart: e.target.value })}
              />
            </div>
          </div>

          <div className="form-group">
            <label className="form-label">Theme</label>
            <input
              type="text"
              placeholder="e.g. Navigating the Future"
              className="form-input"
              value={editMeetingForm.theme}
              onChange={(e) => setEditMeetingForm({ ...editMeetingForm, theme: e.target.value })}
            />
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12 }}>
            <div className="form-group">
              <label className="form-label">Meeting Type</label>
              <select
                className="form-select"
                value={editMeetingForm.meetingType}
                onChange={(e) => setEditMeetingForm({ ...editMeetingForm, meetingType: e.target.value as MeetingType })}
              >
                <option value="REGULAR">REGULAR</option>
                <option value="SPECIAL">SPECIAL</option>
                <option value="CONTEST">CONTEST</option>
              </select>
            </div>
            <div className="form-group">
              <label className="form-label">Meeting Status</label>
              <select
                className="form-select"
                value={editMeetingForm.status}
                onChange={(e) => setEditMeetingForm({ ...editMeetingForm, status: e.target.value as MeetingStatus })}
              >
                <option value="SCHEDULED">SCHEDULED</option>
                <option value="IN_PROGRESS">IN_PROGRESS</option>
                <option value="COMPLETED">COMPLETED</option>
                <option value="CANCELLED">CANCELLED</option>
              </select>
            </div>
          </div>

          <div className="form-group">
            <label className="form-label">Location / Venue</label>
            <input
              type="text"
              placeholder="e.g. Rathinam College Campus"
              className="form-input"
              value={editMeetingForm.location}
              onChange={(e) => setEditMeetingForm({ ...editMeetingForm, location: e.target.value })}
            />
          </div>

          <div className="form-group">
            <label className="form-label">Online Meeting URL</label>
            <input
              type="url"
              placeholder="https://zoom.us/j/..."
              className="form-input"
              value={editMeetingForm.meetingUrl}
              onChange={(e) => setEditMeetingForm({ ...editMeetingForm, meetingUrl: e.target.value })}
            />
          </div>

          <div className="form-group">
            <label className="form-label">Description / Agenda Notes</label>
            <textarea
              className="form-input"
              rows={3}
              style={{ resize: 'vertical' }}
              value={editMeetingForm.description}
              onChange={(e) => setEditMeetingForm({ ...editMeetingForm, description: e.target.value })}
            />
          </div>

          <div style={{ display: 'flex', gap: 10, marginTop: 16 }}>
            <Button
              type="submit"
              variant="gold"
              style={{ flex: 1 }}
              disabled={updatingMeeting || !editMeetingForm.meetingStart || editMeetingForm.meetingNumber < 1}
            >
              {updatingMeeting ? 'Saving Changes...' : 'Save Meeting Changes'}
            </Button>
            <Button type="button" variant="secondary" onClick={() => setIsEditMeetingModalOpen(false)} disabled={updatingMeeting}>
              Cancel
            </Button>
          </div>
        </form>
      </Modal>
    </AppLayout>
  );
};
