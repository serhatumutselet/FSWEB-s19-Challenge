import { useEffect, useState } from "react";
import "./App.css";
import {
  register,
  login,
  getAllTweets,
  getOwnFeed,
  getTweetsByUserId,
  createTweet,
  likeTweet,
  retweet,
  commentOnTweet,
  deleteComment,
  getCommentsByTweetId,
  getLikeCount,
  isLiked,
  dislikeTweet,
  getRetweetCount,
  isRetweeted,
  deleteRetweet,
  getMe,
  deleteTweet,
} from "./api.js";

const TOKEN_KEY = "twitter_token";

export default function App() {
  const [path, setPath] = useState(() => window.location.pathname || "/");
  const [token, setToken] = useState(localStorage.getItem(TOKEN_KEY) || "");

  const [status, setStatus] = useState("Hazir.");
  const [busy, setBusy] = useState(false);

  // auth inputs
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [me, setMe] = useState(null);

  // tweet
  const [tweetText, setTweetText] = useState("");

  // feed + pages
  const [feedMode, setFeedMode] = useState("all"); // all | userId
  const [userId, setUserId] = useState("1"); // for userId page
  const [tweets, setTweets] = useState([]);
  const [commentDrafts, setCommentDrafts] = useState({});
  const [commentsByTweetId, setCommentsByTweetId] = useState({});
  const [likeCountsByTweetId, setLikeCountsByTweetId] = useState({});
  const [likedByTweetId, setLikedByTweetId] = useState({});
  const [retweetCountsByTweetId, setRetweetCountsByTweetId] = useState({});
  const [retweetedByTweetId, setRetweetedByTweetId] = useState({});

  const tokenState = token ? "AUTH" : "GUEST";

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
    if (path === "/login" || path === "/register") return;
    if (!token) navigate("/login");
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [path, token]);

  const loadFeed = async () => {
    if (!token) {
      setStatus("Login olmadan feed cekemezsin (JWT protected).");
      return;
    }
    setBusy(true);
    try {
      const data =
        feedMode === "all" ? await getAllTweets(token) : await getTweetsByUserId(token, userId);
      const newTweets = Array.isArray(data) ? data : [];
      setTweets(newTweets);

      const metas = await Promise.all(
        newTweets.map(async (t) => {
          const tweetId = t.id;
          const [
            comments,
            likeCount,
            liked,
            retweetCount,
            retweeted,
          ] = await Promise.all([
            getCommentsByTweetId(token, tweetId).catch(() => []),
            getLikeCount(token, tweetId).catch(() => 0),
            isLiked(token, tweetId).catch(() => false),
            getRetweetCount(token, tweetId).catch(() => 0),
            isRetweeted(token, tweetId).catch(() => false),
          ]);

          return { tweetId, comments, likeCount, liked, retweetCount, retweeted };
        })
      );

      const nextComments = {};
      const nextLikeCounts = {};
      const nextLiked = {};
      const nextRetweetCounts = {};
      const nextRetweeted = {};

      for (const m of metas) {
        nextComments[m.tweetId] = m.comments || [];
        nextLikeCounts[m.tweetId] = m.likeCount || 0;
        nextLiked[m.tweetId] = Boolean(m.liked);
        nextRetweetCounts[m.tweetId] = m.retweetCount || 0;
        nextRetweeted[m.tweetId] = Boolean(m.retweeted);
      }

      setCommentsByTweetId(nextComments);
      setLikeCountsByTweetId(nextLikeCounts);
      setLikedByTweetId(nextLiked);
      setRetweetCountsByTweetId(nextRetweetCounts);
      setRetweetedByTweetId(nextRetweeted);

      setStatus(`Feed yuklendi (${newTweets.length} tweet).`);
    } catch (e) {
      setStatus(`Feed hatasi: ${e.message}`);
    } finally {
      setBusy(false);
    }
  };

  useEffect(() => {
    if (path !== "/") return;
    if (!token) return;
    loadFeed();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [path, token, feedMode]);

  useEffect(() => {
    if (!token) {
      setMe(null);
      return;
    }
    getMe(token)
      .then((u) => setMe(u))
      .catch(() => setMe(null));
  }, [token]);

  const doRegister = async () => {
    setBusy(true);
    try {
      const res = await register(username, password);
      setStatus(`Register OK: ${typeof res === "string" ? res : JSON.stringify(res)}`);
      navigate("/login");
    } catch (e) {
      setStatus(`Register hata: ${e.message}`);
    } finally {
      setBusy(false);
    }
  };

  const doLogin = async () => {
    setBusy(true);
    try {
      const data = await login(username, password);
      localStorage.setItem(TOKEN_KEY, data.token);
      setToken(data.token);
      setStatus("Login OK. Token alindi.");
      // best-effort: keep typed username too
      navigate("/");
    } catch (e) {
      setStatus(`Login hata: ${e.message}`);
    } finally {
      setBusy(false);
    }
  };

  const loadOwnPage = async () => {
    if (!token) return;
    setFeedMode("all");
    setBusy(true);
    try {
      const data = await getOwnFeed(token);
      setTweets(Array.isArray(data) ? data : []);
      setStatus(`Own Page yuklendi (${Array.isArray(data) ? data.length : 0} tweet).`);
    } catch (e) {
      setStatus(`Own Page hatasi: ${e.message}`);
    } finally {
      setBusy(false);
    }
  };

  const loadUserById = async () => {
    if (!token) return;
    setBusy(true);
    try {
      const data = await getTweetsByUserId(token, userId);
      setTweets(Array.isArray(data) ? data : []);
      setStatus(`User ${userId} tweetleri yuklendi (${Array.isArray(data) ? data.length : 0} tweet).`);
    } catch (e) {
      setStatus(`User tweet hatasi: ${e.message}`);
    } finally {
      setBusy(false);
    }
  };

  const doDeleteTweet = async (tweetId) => {
    setBusy(true);
    try {
      await deleteTweet(token, tweetId);
      setStatus(`Tweet silindi (id=${tweetId})`);
      await loadFeed();
    } catch (e) {
      setStatus(`Silme hatasi: ${e.message}`);
    } finally {
      setBusy(false);
    }
  };

  const doLogout = () => {
    localStorage.removeItem(TOKEN_KEY);
    setToken("");
    setTweets([]);
    setCommentDrafts({});
    setCommentsByTweetId({});
    setLikeCountsByTweetId({});
    setLikedByTweetId({});
    setRetweetCountsByTweetId({});
    setRetweetedByTweetId({});
    setStatus("Logout OK.");
    navigate("/login");
  };

  const doTweet = async () => {
    if (!token) {
      setStatus("Tweet atmak icin login ol.");
      return;
    }
    if (!tweetText.trim()) {
      setStatus("Tweet bos olamaz.");
      return;
    }
    setBusy(true);
    try {
      await createTweet(token, tweetText);
      setTweetText("");
      setStatus("Tweet atildi.");
      await loadFeed();
    } catch (e) {
      setStatus(`Tweet hata: ${e.message}`);
    } finally {
      setBusy(false);
    }
  };

  const doLike = async (tweetId) => {
    try {
      setBusy(true);
      const currentlyLiked = await isLiked(token, tweetId);
      const shouldLike = !currentlyLiked;
      if (shouldLike) await likeTweet(token, tweetId);
      else await dislikeTweet(token, tweetId);
      setStatus(shouldLike ? `Like OK (tweetId=${tweetId})` : `Like silindi (tweetId=${tweetId})`);
      await loadFeed();
    } catch (e) {
      setStatus(`Like hata: ${e.message}`);
    } finally {
      setBusy(false);
    }
  };

  const doRetweet = async (tweetId) => {
    try {
      setBusy(true);
      const currentlyRetweeted = await isRetweeted(token, tweetId);
      const shouldRetweet = !currentlyRetweeted;
      if (shouldRetweet) await retweet(token, tweetId);
      else await deleteRetweet(token, tweetId);
      setStatus(shouldRetweet ? `Retweet OK (tweetId=${tweetId})` : `Retweet geri alindi (tweetId=${tweetId})`);
      await loadFeed();
    } catch (e) {
      setStatus(`Retweet hata: ${e.message}`);
    } finally {
      setBusy(false);
    }
  };

  const doComment = async (tweetId) => {
    const content = (commentDrafts[tweetId] || "").trim();
    if (!content) {
      setStatus("Yorum bos olamaz.");
      return;
    }
    setBusy(true);
    try {
      await commentOnTweet(token, tweetId, content);
      setCommentDrafts((p) => ({ ...p, [tweetId]: "" }));
      setStatus(`Yorum OK (tweetId=${tweetId})`);
      await loadFeed();
    } catch (e) {
      setStatus(`Yorum hata: ${e.message}`);
    } finally {
      setBusy(false);
    }
  };

  const doDeleteComment = async (commentId) => {
    setBusy(true);
    try {
      await deleteComment(token, commentId);
      setStatus(`Yorum silindi (id=${commentId})`);
      await loadFeed();
    } catch (e) {
      setStatus(`Yorum silme hatasi: ${e.message}`);
    } finally {
      setBusy(false);
    }
  };

  const LoginView = (
    <div className="authWrap">
      <div className="authCard">
        <div className="authTitle">Giris Yap</div>
        <div className="authSubtitle">JWT almak icin login olun.</div>

        <label>
          Username
          <input value={username} onChange={(e) => setUsername(e.target.value)} />
        </label>
        <label>
          Password
          <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} />
        </label>

        <div className="row">
          <button className="btn" onClick={doLogin} disabled={busy || !username || !password}>
            Login
          </button>
          <button className="btn btnGhost" onClick={() => navigate("/register")} disabled={busy}>
            Register'a git
          </button>
        </div>

        <div className="status">{status}</div>
      </div>
    </div>
  );

  const RegisterView = (
    <div className="authWrap">
      <div className="authCard">
        <div className="authTitle">Kayit Ol</div>
        <div className="authSubtitle">Yeni kullanici olusturun.</div>

        <label>
          Username
          <input value={username} onChange={(e) => setUsername(e.target.value)} />
        </label>
        <label>
          Password
          <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} />
        </label>

        <div className="row">
          <button className="btn" onClick={doRegister} disabled={busy || !username || !password}>
            Register
          </button>
          <button className="btn btnGhost" onClick={() => navigate("/login")} disabled={busy}>
            Login'a dön
          </button>
        </div>

        <div className="status">{status}</div>
      </div>
    </div>
  );

  const HomeView = (
    <div className="wrap">
      <div className="top">
        <div className="brand">Twitter Clone UI</div>
        <div className="topRight">
          <div className="pill">{me?.username ? `@${me.username}` : tokenState}</div>
          <button className="btn btnGhost small" onClick={() => navigate("/own")} disabled={busy || !token}>
            Own Page
          </button>
          <button className="btn btnGhost small" onClick={() => navigate("/user")} disabled={busy || !token}>
            User by Id
          </button>
          <button className="btn btnGhost small" onClick={doLogout} disabled={busy || !token}>
            Logout
          </button>
        </div>
      </div>

      <div className="grid">
        <div className="side">
          <div className="card">
            <h3>Tweet at</h3>
            <label>
              Content (max 280)
              <textarea value={tweetText} onChange={(e) => setTweetText(e.target.value)} maxLength={280} />
            </label>
            <div className="row between">
              <span className="muted">{tweetText.length}/280</span>
              <button className="btn" onClick={doTweet} disabled={busy || !token}>
                POST /tweet
              </button>
            </div>
          </div>
          <div className="status">{status}</div>
        </div>

        <div className="main">
          <div className="card">
            <h3>Feed</h3>
            <div className="row">
              <button className="btn btnGhost" onClick={loadFeed} disabled={busy || !token}>
                Load All (findAll)
              </button>
              <button className="btn btnGhost" onClick={loadOwnPage} disabled={busy || !token}>
                Load Own Page
              </button>
            </div>

            <ul className="tweetList">
              {tweets.map((t) => (
                <li key={t.id} className="tweet">
                  <div className="tweetHead">
                    <span>@{t.user?.username || "unknown"}</span>
                    <span>#{t.id}</span>
                    {retweetedByTweetId[t.id] ? <span className="badge">Retweeted</span> : null}
                    {me?.username && t.user?.username === me.username ? (
                      <button className="btn btnGhost small" onClick={() => doDeleteTweet(t.id)} disabled={busy}>
                        Delete
                      </button>
                    ) : null}
                  </div>
                  <div className="tweetBody">{t.content}</div>

                  <div className="actions">
                    <button className="btn btnGhost small" onClick={() => doLike(t.id)} disabled={busy || !token}>
                      {likedByTweetId[t.id] ? `Liked (${likeCountsByTweetId[t.id] || 0})` : `Like (${likeCountsByTweetId[t.id] || 0})`}
                    </button>
                    <button className="btn btnGhost small" onClick={() => doRetweet(t.id)} disabled={busy || !token}>
                      {retweetedByTweetId[t.id] ? `Retweeted (${retweetCountsByTweetId[t.id] || 0})` : `Retweet (${retweetCountsByTweetId[t.id] || 0})`}
                    </button>
                  </div>

                  <div className="commentRow">
                    <input
                      placeholder="Yorum yaz..."
                      value={commentDrafts[t.id] || ""}
                      onChange={(e) => setCommentDrafts((p) => ({ ...p, [t.id]: e.target.value }))}
                    />
                    <button className="btn btnGhost small" onClick={() => doComment(t.id)} disabled={busy || !token}>
                      POST /comment
                    </button>
                  </div>

                  <div className="commentList">
                    {(commentsByTweetId[t.id] || []).length === 0 ? (
                      <div className="commentEmpty">Henüz yorum yok.</div>
                    ) : (
                      commentsByTweetId[t.id].map((c) => (
                        <div key={c.id} className="commentItem">
                          <div className="commentItemTop">
                            <span className="commentUser">@{c.user?.username || "unknown"}</span>
                            {(me?.username && (c.user?.username === me.username || t.user?.username === me.username)) ? (
                              <button
                                className="btn btnGhost small"
                                onClick={() => doDeleteComment(c.id)}
                                disabled={busy}
                              >
                                Yorum Sil
                              </button>
                            ) : null}
                          </div>
                          <span className="commentContent">{c.content}</span>
                        </div>
                      ))
                    )}
                  </div>
                </li>
              ))}
            </ul>
          </div>
        </div>
      </div>
    </div>
  );

  const OwnPageView = (
    <div className="wrap">
      <div className="top">
        <div className="brand">Own Page</div>
        <div className="topRight">
          <button className="btn btnGhost small" onClick={() => navigate("/")} disabled={busy}>
            Home
          </button>
          <button className="btn btnGhost small" onClick={loadOwnPage} disabled={busy || !token}>
            Refresh
          </button>
          <button className="btn btnGhost small" onClick={doLogout} disabled={busy || !token}>
            Logout
          </button>
        </div>
      </div>
      <div className="card">
        <div className="status">{status}</div>
        <button className="btn btnGhost" onClick={loadOwnPage} disabled={busy || !token}>
          Load Own Feed
        </button>
      </div>
      {/* reuse HomeView tweet list */}
      <div className="card">
        <ul className="tweetList">
          {tweets.map((t) => (
            <li key={t.id} className="tweet">
              <div className="tweetHead">
                <span>@{t.user?.username || "unknown"}</span>
                <span>#{t.id}</span>
                {retweetedByTweetId[t.id] ? <span className="badge">Retweeted</span> : null}
                {me?.username && t.user?.username === me.username ? (
                  <button className="btn btnGhost small" onClick={() => doDeleteTweet(t.id)} disabled={busy}>
                    Delete
                  </button>
                ) : null}
              </div>
              <div className="tweetBody">{t.content}</div>
            </li>
          ))}
        </ul>
      </div>
    </div>
  );

  const UserByIdView = (
    <div className="wrap">
      <div className="top">
        <div className="brand">User Tweets by Id</div>
        <div className="topRight">
          <button className="btn btnGhost small" onClick={() => navigate("/")} disabled={busy}>
            Home
          </button>
          <button className="btn btnGhost small" onClick={doLogout} disabled={busy || !token}>
            Logout
          </button>
        </div>
      </div>
      <div className="card">
        <div className="row">
          <input value={userId} onChange={(e) => setUserId(e.target.value)} placeholder="userId" />
          <button className="btn btnGhost" onClick={loadUserById} disabled={busy || !token}>
            Load
          </button>
        </div>
        <div className="status">{status}</div>
      </div>
      <div className="card">
        <ul className="tweetList">
          {tweets.map((t) => (
            <li key={t.id} className="tweet">
              <div className="tweetHead">
                <span>@{t.user?.username || "unknown"}</span>
                <span>#{t.id}</span>
              </div>
              <div className="tweetBody">{t.content}</div>
            </li>
          ))}
        </ul>
      </div>
    </div>
  );

  if (path === "/login") return LoginView;
  if (path === "/register") return RegisterView;
  if (path === "/own") return OwnPageView;
  if (path === "/user") return UserByIdView;
  return HomeView;
}
