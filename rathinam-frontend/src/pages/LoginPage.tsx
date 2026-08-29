import React, { useState, useRef, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { 
  Mail, 
  Lock, 
  Eye, 
  EyeOff, 
  ArrowRight, 
  ShieldCheck, 
  AlertCircle, 
  CheckCircle2, 
  User, 
  Award,
  Mic
} from 'lucide-react';
import '../styles/LoginPage.css';

export const LoginPage: React.FC = () => {
  // Single Card Toggle State: false = Sign In, true = Sign Up
  const [isSignUp, setIsSignUp] = useState(false);

  // Sign In Form States
  const [signInEmail, setSignInEmail] = useState('');
  const [signInPassword, setSignInPassword] = useState('');
  const [showSignInPassword, setShowSignInPassword] = useState(false);
  const [signInError, setSignInError] = useState<string | null>(null);
  const [isSubmittingSignIn, setIsSubmittingSignIn] = useState(false);
  const [signInSuccess, setSignInSuccess] = useState(false);

  // Sign Up Form States
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [signUpEmail, setSignUpEmail] = useState('');
  const [signUpPassword, setSignUpPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [showSignUpPassword, setShowSignUpPassword] = useState(false);
  const [signUpError, setSignUpError] = useState<string | null>(null);
  const [isSubmittingSignUp, setIsSubmittingSignUp] = useState(false);
  const [signUpSuccess, setSignUpSuccess] = useState(false);

  // 3D Tilt Parallax State
  const cardRef = useRef<HTMLDivElement>(null);
  const [tilt, setTilt] = useState({ rotateX: 0, rotateY: 0 });
  const [isHovered, setIsHovered] = useState(false);
  const [isTouchDevice, setIsTouchDevice] = useState(false);

  const { login, register } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    const checkTouch = () => {
      const hasTouch = 'ontouchstart' in window || navigator.maxTouchPoints > 0;
      const reducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
      setIsTouchDevice(hasTouch || reducedMotion);
    };
    checkTouch();
  }, []);

  const handleMouseMove = (e: React.MouseEvent<HTMLDivElement>) => {
    if (isTouchDevice || !cardRef.current) return;

    const card = cardRef.current;
    const rect = card.getBoundingClientRect();
    const x = e.clientX - rect.left;
    const y = e.clientY - rect.top;
    const centerX = rect.width / 2;
    const centerY = rect.height / 2;

    const rotateX = ((y - centerY) / centerY) * -2;
    const rotateY = ((x - centerX) / centerX) * 2;

    setTilt({ rotateX, rotateY });
  };

  const handleMouseEnter = () => {
    if (!isTouchDevice) setIsHovered(true);
  };

  const handleMouseLeave = () => {
    setIsHovered(false);
    setTilt({ rotateX: 0, rotateY: 0 });
  };

  // Sign In Submission Handler
  const handleSignInSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSignInError(null);
    setIsSubmittingSignIn(true);

    try {
      await login(signInEmail, signInPassword);
      setSignInSuccess(true);
      setTimeout(() => {
        navigate('/dashboard');
      }, 400);
    } catch (err: any) {
      setSignInError(err.message || 'Invalid email or password. Please try again.');
    } finally {
      setIsSubmittingSignIn(false);
    }
  };

  // Sign Up Submission Handler
  const handleSignUpSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSignUpError(null);

    if (signUpPassword !== confirmPassword) {
      setSignUpError('Passwords do not match. Please verify your password.');
      return;
    }

    setIsSubmittingSignUp(true);

    try {
      await register({
        firstName,
        lastName,
        email: signUpEmail,
        pass: signUpPassword
      });
      setSignUpSuccess(true);
      setTimeout(() => {
        navigate('/dashboard');
      }, 400);
    } catch (err: any) {
      setSignUpError(err.message || 'Registration failed. Please verify your details.');
      setIsSubmittingSignUp(false);
    }
  };

  return (
    <div className="auth-page-container">
      {/* Toastmasters International Corner Logo */}
      <img
        src="/assets/toastmasters-logo.png"
        alt="Toastmasters International"
        className="auth-corner-logo"
      />

      {/* Toastmasters International Logo Background Watermark */}
      <img
        src="/assets/toastmasters-logo.png"
        alt=""
        className="auth-watermark-bg"
        aria-hidden="true"
      />

      {/* Grid Pattern overlay */}
      <div className="auth-bg-grid" />

      {/* Top Header Branding with Rathinam Logo (Attached Image 1) */}
      <div className="auth-top-header">
        <img
          src="/assets/rathinam-logo.png"
          alt="Rathinam Group of Institutions"
          className="auth-rathinam-logo"
        />
        <div className="auth-header-title-group">
          <span className="auth-header-club-name">Rathinam Toastmasters Club</span>
          <span className="auth-header-district-badge">RTC — District 230</span>
        </div>
      </div>

      {/* Minimal 3D Interactive Light Auth Card */}
      <div
        className="auth-card-wrapper"
        onMouseMove={handleMouseMove}
        onMouseEnter={handleMouseEnter}
        onMouseLeave={handleMouseLeave}
        style={{
          transform: isHovered
            ? `perspective(1000px) rotateX(${tilt.rotateX}deg) rotateY(${tilt.rotateY}deg)`
            : 'perspective(1000px) rotateX(0deg) rotateY(0deg)',
        }}
      >
        <div
          ref={cardRef}
          className={`auth-split-card ${isSignUp ? 'sign-up-mode' : ''}`}
        >
          {/* Form Panels Container */}
          <div className="auth-forms-container">
            {/* ==================== SIGN IN FORM (LEFT PANEL) ==================== */}
            <div className="auth-form-panel sign-in-panel">
              <div className="auth-header">
                <h1 className="auth-title">Welcome Back</h1>
                <p className="auth-subtitle">
                  Continue your Toastmasters journey. Manage meetings, track performance, and grow together.
                </p>
              </div>

              {signInError && (
                <div className="auth-error-alert" role="alert">
                  <AlertCircle size={18} />
                  <span>{signInError}</span>
                </div>
              )}

              <form className="auth-form" onSubmit={handleSignInSubmit} noValidate>
                <div className="auth-field-group">
                  <label className="auth-field-label" htmlFor="signin-email">Email Address</label>
                  <div className="auth-input-relative">
                    <input
                      id="signin-email"
                      type="email"
                      required
                      autoComplete="email"
                      className="auth-input"
                      placeholder="member@rathinam.com"
                      value={signInEmail}
                      onChange={(e) => setSignInEmail(e.target.value)}
                      disabled={isSubmittingSignIn || signInSuccess}
                    />
                    <Mail size={18} className="auth-input-icon" />
                  </div>
                </div>

                <div className="auth-field-group">
                  <label className="auth-field-label" htmlFor="signin-password">Password</label>
                  <div className="auth-input-relative">
                    <input
                      id="signin-password"
                      type={showSignInPassword ? 'text' : 'password'}
                      required
                      autoComplete="current-password"
                      className="auth-input"
                      placeholder="••••••••"
                      value={signInPassword}
                      onChange={(e) => setSignInPassword(e.target.value)}
                      disabled={isSubmittingSignIn || signInSuccess}
                    />
                    <Lock size={18} className="auth-input-icon" />
                    <button
                      type="button"
                      className="auth-toggle-pw"
                      onClick={() => setShowSignInPassword(!showSignInPassword)}
                      aria-label={showSignInPassword ? 'Hide password' : 'Show password'}
                    >
                      {showSignInPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                    </button>
                  </div>
                </div>

                <button
                  type="submit"
                  className={`auth-submit-btn ${signInSuccess ? 'btn-success' : ''}`}
                  disabled={isSubmittingSignIn || signInSuccess}
                >
                  {isSubmittingSignIn ? (
                    <>
                      <div className="auth-spinner" aria-hidden="true" />
                      <span>Signing In...</span>
                    </>
                  ) : signInSuccess ? (
                    <>
                      <CheckCircle2 size={18} />
                      <span>Access Granted</span>
                    </>
                  ) : (
                    <>
                      <span>Sign In</span>
                      <ArrowRight size={18} />
                    </>
                  )}
                </button>
              </form>

              <div className="auth-footer-note">
                <ShieldCheck size={14} color="#10B981" />
                <span>256-bit Encrypted Session • Toastmasters International</span>
              </div>
            </div>

            {/* ==================== SIGN UP FORM (RIGHT PANEL) ==================== */}
            <div className="auth-form-panel sign-up-panel">
              <div className="auth-header">
                <h1 className="auth-title">Join Rathinam Toastmasters Club</h1>
                <p className="auth-subtitle">
                  Build confidence, develop leadership skills, and grow together with your club.
                </p>
              </div>

              {signUpError && (
                <div className="auth-error-alert" role="alert">
                  <AlertCircle size={18} />
                  <span>{signUpError}</span>
                </div>
              )}

              <form className="auth-form" onSubmit={handleSignUpSubmit} noValidate>
                <div className="auth-form-row">
                  <div className="auth-field-group">
                    <label className="auth-field-label" htmlFor="signup-firstname">First Name</label>
                    <div className="auth-input-relative">
                      <input
                        id="signup-firstname"
                        type="text"
                        required
                        className="auth-input"
                        placeholder="John"
                        value={firstName}
                        onChange={(e) => setFirstName(e.target.value)}
                        disabled={isSubmittingSignUp || signUpSuccess}
                      />
                      <User size={18} className="auth-input-icon" />
                    </div>
                  </div>

                  <div className="auth-field-group">
                    <label className="auth-field-label" htmlFor="signup-lastname">Last Name</label>
                    <div className="auth-input-relative">
                      <input
                        id="signup-lastname"
                        type="text"
                        required
                        className="auth-input"
                        placeholder="Doe"
                        value={lastName}
                        onChange={(e) => setLastName(e.target.value)}
                        disabled={isSubmittingSignUp || signUpSuccess}
                      />
                      <User size={18} className="auth-input-icon" />
                    </div>
                  </div>
                </div>

                <div className="auth-field-group">
                  <label className="auth-field-label" htmlFor="signup-email">Email Address</label>
                  <div className="auth-input-relative">
                    <input
                      id="signup-email"
                      type="email"
                      required
                      autoComplete="email"
                      className="auth-input"
                      placeholder="member@rathinam.com"
                      value={signUpEmail}
                      onChange={(e) => setSignUpEmail(e.target.value)}
                      disabled={isSubmittingSignUp || signUpSuccess}
                    />
                    <Mail size={18} className="auth-input-icon" />
                  </div>
                </div>

                <div className="auth-form-row">
                  <div className="auth-field-group">
                    <label className="auth-field-label" htmlFor="signup-password">Password</label>
                    <div className="auth-input-relative">
                      <input
                        id="signup-password"
                        type={showSignUpPassword ? 'text' : 'password'}
                        required
                        autoComplete="new-password"
                        className="auth-input"
                        placeholder="••••••••"
                        value={signUpPassword}
                        onChange={(e) => setSignUpPassword(e.target.value)}
                        disabled={isSubmittingSignUp || signUpSuccess}
                      />
                      <Lock size={18} className="auth-input-icon" />
                    </div>
                  </div>

                  <div className="auth-field-group">
                    <label className="auth-field-label" htmlFor="signup-confirmpassword">Confirm</label>
                    <div className="auth-input-relative">
                      <input
                        id="signup-confirmpassword"
                        type={showSignUpPassword ? 'text' : 'password'}
                        required
                        autoComplete="new-password"
                        className="auth-input"
                        placeholder="••••••••"
                        value={confirmPassword}
                        onChange={(e) => setConfirmPassword(e.target.value)}
                        disabled={isSubmittingSignUp || signUpSuccess}
                      />
                      <Lock size={18} className="auth-input-icon" />
                      <button
                        type="button"
                        className="auth-toggle-pw"
                        onClick={() => setShowSignUpPassword(!showSignUpPassword)}
                        aria-label={showSignUpPassword ? 'Hide password' : 'Show password'}
                      >
                        {showSignUpPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                      </button>
                    </div>
                  </div>
                </div>

                <button
                  type="submit"
                  className={`auth-submit-btn ${signUpSuccess ? 'btn-success' : ''}`}
                  disabled={isSubmittingSignUp || signUpSuccess}
                >
                  {isSubmittingSignUp ? (
                    <>
                      <div className="auth-spinner" aria-hidden="true" />
                      <span>Creating Account...</span>
                    </>
                  ) : signUpSuccess ? (
                    <>
                      <CheckCircle2 size={18} />
                      <span>Account Created</span>
                    </>
                  ) : (
                    <>
                      <span>Sign Up & Join</span>
                      <ArrowRight size={18} />
                    </>
                  )}
                </button>
              </form>

              <div className="auth-footer-note">
                <ShieldCheck size={14} color="#10B981" />
                <span>RTC — District 230 • Rathinam Toastmasters Club</span>
              </div>
            </div>
          </div>

          {/* ==================== SLIDING OVERLAY PROMOTIONAL PANEL ==================== */}
          <div className="auth-overlay-wrapper">
            <div className="auth-overlay">
              {/* Overlay Panel (Left Position - Shown in Sign Up Mode) */}
              <div className="overlay-panel overlay-left">
                <div className="overlay-emblem-badge">
                  <Mic size={26} />
                </div>
                <h2 className="overlay-title">Welcome Back!</h2>
                <p className="overlay-text">
                  Already a member of Rathinam Toastmasters Club? Sign in to access your meeting schedules, speech records, and performance metrics.
                </p>
                <button
                  type="button"
                  className="overlay-toggle-btn"
                  onClick={() => setIsSignUp(false)}
                >
                  SIGN IN
                </button>
              </div>

              {/* Overlay Panel (Right Position - Shown in Sign In Mode) */}
              <div className="overlay-panel overlay-right">
                <div className="overlay-emblem-badge">
                  <Award size={26} />
                </div>
                <h2 className="overlay-title">Join Our Club!</h2>
                <p className="overlay-text">
                  Embark on your journey to public speaking and leadership excellence with RTC — District 230.
                </p>
                <button
                  type="button"
                  className="overlay-toggle-btn"
                  onClick={() => setIsSignUp(true)}
                >
                  SIGN UP
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
