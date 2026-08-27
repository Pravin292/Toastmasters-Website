export type AccountRole = 'ADMIN' | 'PRESIDENT' | 'OFFICER' | 'MEMBER';

export interface AuthResponse {
  token: string;
  email: string;
  role: AccountRole;
  memberId?: string;
  firstName?: string;
  lastName?: string;
}

export interface Member {
  id: string;
  accountId?: string;
  firstName: string;
  lastName: string;
  displayName?: string;
  email: string;
  phoneNumber?: string;
  profilePictureUrl?: string;
  joinDate: string;
  status: string;
  bio?: string;
  createdAt?: string;
  updatedAt?: string;
}

export type MeetingStatus = 'SCHEDULED' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED';
export type MeetingType = 'REGULAR' | 'SPECIAL' | 'CONTEST';

export interface Meeting {
  id: string;
  meetingNumber: number;
  meetingStart: string;
  meetingEnd?: string;
  theme?: string;
  meetingType: MeetingType;
  status: MeetingStatus;
  location?: string;
  meetingUrl?: string;
  description?: string;
  createdAt?: string;
}

export interface MeetingWorkflow {
  meetingId: string;
  meetingNumber: number;
  meetingStart: string;
  theme?: string;
  meetingType: MeetingType;
  status: MeetingStatus;
  canStart: boolean;
  canComplete: boolean;
  attendanceSummary: {
    totalRecords: number;
    presentCount: number;
    absentCount: number;
    excusedCount: number;
    attendancePercentage: number;
  };
  roleSummary: {
    rolesAssigned: number;
    rolesFilled: number;
    rolesRemaining: number;
  };
  pointsSummary: {
    totalPointsAwarded: number;
  };
  workflowWarnings: string[];
  isAiSummaryAvailable: boolean;
}

export type AttendanceStatus = 'PRESENT' | 'ABSENT' | 'EXCUSED';

export interface Attendance {
  id: string;
  meetingId: string;
  memberId: string;
  memberName?: string;
  memberDisplayName?: string;
  memberEmail?: string;
  status: AttendanceStatus;
  checkInTime?: string;
}

export interface RoleDefinition {
  id: string;
  name: string;
  description?: string;
  active: boolean;
}

export interface RoleAssignment {
  id: string;
  meetingId: string;
  roleDefinitionId: string;
  roleName: string;
  memberId: string;
  memberDisplayName: string;
}

export interface PointRule {
  id: string;
  code: string;
  name: string;
  description?: string;
  points: number;
  active?: boolean;
  isActive?: boolean;
  category?: string;
}

export interface PointEvent {
  id: string;
  memberId: string;
  memberDisplayName?: string;
  memberEmail?: string;
  meetingId?: string;
  meetingNumber?: number;
  pointRuleId?: string;
  pointRuleCode?: string;
  pointRuleName?: string;
  points: number;
  reason?: string;
  sourceType?: string;
  createdAt: string;
}

export interface MemberPointsSummary {
  memberId: string;
  memberDisplayName?: string;
  memberEmail?: string;
  totalPoints: number;
  events?: {
    content: PointEvent[];
    totalElements?: number;
    totalPages?: number;
  };
}

export interface RankingEntry {
  rank?: number;
  memberId: string;
  displayName: string;
  email?: string;
  points: number;
}

export interface MonthlyChampion {
  year: number;
  month: number;
  memberId: string;
  displayName: string;
  email?: string;
  points: number;
}

export interface MonthlyRanking {
  year: number;
  month: number;
  totalMembers?: number;
  leaderboard?: {
    content: RankingEntry[];
    totalElements?: number;
    totalPages?: number;
  };
  champion?: MonthlyChampion | null;
}

export interface MemberRanking {
  memberId: string;
  displayName: string;
  totalPoints: number;
  rank?: number;
  year?: number;
  month?: number;
}

export interface Achievement {
  id: string;
  memberId?: string;
  memberDisplayName?: string;
  achievementDefinitionId?: string;
  achievementCode?: string;
  achievementName?: string;
  name?: string;
  description?: string;
  icon?: string;
  category?: string;
  earnedAt?: string;
  meetingId?: string;
  reason?: string;
}

export interface Badge {
  achievementId: string;
  memberId: string;
  badgeName: string;
  description: string;
  icon: string;
  category: string;
  earnedAt: string;
}

export interface Certificate {
  id: string;
  certificateNumber: string;
  memberId: string;
  memberDisplayName?: string;
  certificateType: string;
  title: string;
  description?: string;
  issuedDate: string;
  achievementId?: string;
  status?: string;
}

export interface IssueCertificateRequest {
  memberId: string;
  certificateType: string;
  title: string;
  description: string;
  achievementId?: string;
  customCertificateNumber?: string;
}

export interface MemberAnalytics {
  memberId: string;
  displayName: string;
  email: string;
  totalMeetingsAttended: number;
  attendancePercentage: number;
  totalRolesPerformed: number;
  totalPoints: number;
  currentRank?: number;
  achievementsEarned: number;
}

export interface MemberMonthlyPerformance {
  year: number;
  month: number;
  points: number;
  attendanceCount: number;
  rolesCount: number;
}

export interface ClubOverviewAnalytics {
  totalMembers: number;
  activeMembers: number;
  totalMeetings: number;
  totalAttendanceRecords: number;
  averageAttendancePerMeeting: number;
  totalPointsAwarded: number;
  totalAchievementsEarned: number;
  currentMonthlyChampion?: MonthlyChampion | null;
}

export interface MonthlyAnalytics {
  year: number;
  month: number;
  totalMeetings: number;
  totalAttendance: number;
  averageAttendance: number;
  totalPointsAwarded: number;
  activeMembersCount: number;
  topPerformers?: RankingEntry[];
  monthlyChampion?: MonthlyChampion | null;
  achievementsEarned: number;
}

export interface AiSummary {
  summary: string;
  attendanceInsights: string;
  roleInsights: string;
  performanceInsights: string;
  modelName: string;
  generatedAt: string;
}

export interface Notification {
  id: string;
  memberId: string;
  type: string;
  title: string;
  message: string;
  meetingId?: string;
  read: boolean;
  readAt?: string;
  createdAt: string;
}

export interface ApiResponse<T> {
  success: boolean;
  data: T;
  message?: string;
  timestamp?: string;
}
