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
  meetingId?: string;
  points: number;
  reason: string;
  createdAt: string;
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
  code: string;
  name: string;
  description: string;
  icon: string;
  earnedAt?: string;
}

export interface Certificate {
  id: string;
  certificateNumber: string;
  memberId: string;
  title: string;
  certificateType: string;
  issuedDate: string;
}

export interface MemberAnalytics {
  memberId: string;
  memberName: string;
  totalPoints: number;
  rank: number;
  attendanceRate: number;
  totalRolesPlayed: number;
  achievementsCount: number;
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
