import { useEffect, useMemo, useState } from "react";
import "./App.css";

import Sidebar from "./components/Sidebar.jsx";
import AuthCard from "./components/AuthCard.jsx";
import TweetComposer from "./components/TweetComposer.jsx";
import Timeline from "./components/Timeline.jsx";
import {
  commentOnTweet,
  createTweet,
  getAllTweets,
  getTweetsByUserId,
  likeTweet,
  login,
  register,
  retweet,
} from "./api.js";

const TOKEN_KEY = "twitter_token";

export default function AppTwitter() {
  const [token, setToken] = useState(localStorage.getItem(TOKEN_KEY) || "");
  const [path, setPath] = useState(() => window.location.pathname || "/");

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const [tweetContent, setTweetContent] = useState("");
  const [commentDrafts, setCommentDrafts] = useState({});

  const [mode, setMode] = useState("all"); // "all" | "user"
  const [userId, setUserId] = useState("1");

  const [allTweets, setAllTweets] = useState([]);
  const [userTweets, setUserTweets] = useState([]);

  const [loading, setLoading] = useState(false);
  const [status, setStatus] = useState("Hazir");

  const authHeaders = useMemo(
    () => ({
      "Content-Type": "application/json",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    }),
    [token]
  );

  const navigate = (to) => {
    if (window.location.pathname === to) return;
    window.history.pushState({}, "", to);
    setPath(to);
  };

  useEffect(() => {
    const onPop = () => setPath(window.location.pathname || "/");
    window.addEventListener("popstate", onPop);
    return () => window.removeEventListener("popstate", onPop);
  }, []);

  // Route protection (no react-router dependency)
  useEffect(() => {
    const isAuthPage = path === "/login" || path === "/register";
    if (!token && !isAuthPage) navigate("/login");
    if (token && isAuthPage) navigate("/");
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [token, path]);

  const reloadAll = async () => {
    if (!token) return;
    setLoading(true);
    try {
      const [all, user] = await Promise.all([
        getAllTweets(token),
        getTweetsByUserId(token, userId),
      ]);
      setAllTweets(all);
      setUserTweets(user);
      setStatus("Tweetler güncellendi.");
    } catch (error) {
      setStatus(`Yukleme hatasi: ${error.message}`);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (token) {
      reloadAll();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [token]);

  const tweetsToRender = mode === "all" ? allTweets : userTweets;

  const handleRegister = async (u, p) => {
    setStatus("Kayit yapiliyor...");
    await register(u, p);
    setStatus("Kayit basarili. Simdi login olabilirsin.");
    navigate("/login");
  };

  const handleLogin = async (u, p) => {
    setStatus("Login yapiliyor...");
    const data = await login(u, p);
    localStorage.setItem(TOKEN_KEY, data.token);
    setToken(data.token);
    setStatus("Login basarili.");
    navigate("/");
  };

  const handleLogout = () => {
    localStorage.removeItem(TOKEN_KEY);
    setToken("");
    setAllTweets([]);
    setUserTweets([]);
    setTweetContent("");
    setCommentDrafts({});
    setStatus("Cikis yapildi.");
    navigate("/login");
  };

  const onSubmitTweet = async () => {
    if (!token) {
      setStatus("Tweet atmak icin once login ol.");
      return;
    }
    if (!tweetContent.trim()) {
      setStatus("Tweet bos olamaz.");
      return;
    }

    setLoading(true);
    try {
      await createTweet(token, tweetContent);
      setTweetContent("");
      setStatus("Tweet gonderildi.");
      await reloadAll();
    } catch (error) {
      setStatus(`Tweet gonderme hatasi: ${error.message}`);
    } finally {
      setLoading(false);
    }
  };

  const onLike = async (tweetId) => {
    if (!token) return;
    setLoading(true);
    try {
      await likeTweet(token, tweetId);
      setStatus("Like atildi.");
      await reloadAll();
    } catch (error) {
      setStatus(`Like hatasi: ${error.message}`);
    } finally {
      setLoading(false);
    }
  };

  const onRetweet = async (tweetId) => {
    if (!token) return;
    setLoading(true);
    try {
      await retweet(token, tweetId);
      setStatus("Retweet atildi.");
      await reloadAll();
    } catch (error) {
      setStatus(`Retweet hatasi: ${error.message}`);
    } finally {
      setLoading(false);
    }
  };

  const onCommentChange = (tweetId, value) => {
    setCommentDrafts((prev) => ({ ...prev, [tweetId]: value }));
  };

  const onCommentSubmit = async (tweetId) => {
    if (!token) return;
    const content = (commentDrafts[tweetId] || "").trim();
    if (!content) {
      setStatus("Yorum bos olamaz.");
      return;
    }

    setLoading(true);
    try {
      await commentOnTweet(token, tweetId, content);
      setCommentDrafts((prev) => ({ ...prev, [tweetId]: "" }));
      setStatus("Yorum gonderildi.");
      await reloadAll();
    } catch (error) {
      setStatus(`Yorum hatasi: ${error.message}`);
    } finally {
      setLoading(false);
    }
  };

  const isAuthPage = path === "/login" || path === "/register";
  if (isAuthPage) {
    return (
      <div className="twittearApp">
        <div className="topbar">
          <div className="brand">Twittear</div>
          <div className="topRight">
            <span className="tokenState">{token ? "Authenticated" : "Guest"}</span>
          </div>
        </div>

        <div className="body">
          <aside className="leftRail" />
          <main className="centerRail">
            <div className="authWrap">
              <AuthCard
                token={token}
                onLogout={handleLogout}
                onLogin={handleLogin}
                onRegister={handleRegister}
              />
            </div>
          </main>
          <aside className="rightRail" />
        </div>
      </div>
    );
  }

  if (!token) return null;

  return (
    <div className="twittearApp">
      <div className="topbar">
        <div className="brand">Twittear</div>
        <div className="topRight">
          <span className="tokenState">{token ? "Authenticated" : "Guest"}</span>
          {token ? (
            <button className="topBtn" onClick={handleLogout} disabled={loading}>
              Logout
            </button>
          ) : null}
        </div>
      </div>

      <div className="body">
        <aside className="leftRail">
          <Sidebar />
        </aside>

        <main className="centerRail">
          <TweetComposer
            token={token}
            content={tweetContent}
            onChange={(v) => setTweetContent(v)}
            onSubmit={onSubmitTweet}
            loading={loading}
          />

          <Timeline
            mode={mode}
            setMode={setMode}
            userId={userId}
            setUserId={setUserId}
            loading={loading}
            status={status}
            tweets={tweetsToRender}
            onLoadUser={reloadAll}
            onLike={onLike}
            onRetweet={onRetweet}
            commentDrafts={commentDrafts}
            onCommentChange={onCommentChange}
            onCommentSubmit={onCommentSubmit}
          />
        </main>

        <aside className="rightRail">
          <div className="infoCard card">
            <h3>Odev Notlari</h3>
            <p className="muted">
              CORS testi: React (3200) ile Backend (3000).
            </p>
            <p className="muted">
              User timeline: <code>/tweet/findByUserId</code>
            </p>
            <p className="muted">
              Feed: <code>/tweet/findAll</code>
            </p>
          </div>
        </aside>
      </div>
    </div>
  );
}

