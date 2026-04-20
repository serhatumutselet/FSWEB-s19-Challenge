const API_BASE = "http://localhost:3000";

function withHeaders(token, extra = {}) {
  return {
    "Content-Type": "application/json",
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
    ...extra,
  };
}

async function parseError(res) {
  const contentType = res.headers.get("content-type") || "";
  try {
    if (contentType.includes("application/json")) {
      const body = await res.json().catch(() => null);
      if (body?.message) return body.message;
      return body ? JSON.stringify(body) : `HTTP ${res.status}`;
    }
  } catch {
    // ignore and fallback to text
  }

  try {
    const text = await res.text();
    return text || `HTTP ${res.status}`;
  } catch {
    return `HTTP ${res.status}`;
  }
}

export async function register(username, password) {
  const res = await fetch(`${API_BASE}/register`, {
    method: "POST",
    headers: withHeaders(undefined),
    body: JSON.stringify({ username, password }),
  });
  const text = await res.text();
  if (!res.ok) throw new Error(text || `HTTP ${res.status}`);
  return text;
}

export async function login(username, password) {
  const res = await fetch(`${API_BASE}/login`, {
    method: "POST",
    headers: withHeaders(undefined),
    body: JSON.stringify({ username, password }),
  });

  const data = await res.json().catch(() => null);
  if (!res.ok) throw new Error(data?.message || JSON.stringify(data) || `HTTP ${res.status}`);
  return data;
}

export async function getAllTweets(token) {
  const res = await fetch(`${API_BASE}/tweet/findAll`, {
    method: "GET",
    headers: withHeaders(token),
  });

  const data = await res.json().catch(() => null);
  if (!res.ok) {
    throw new Error(data?.message || JSON.stringify(data) || `HTTP ${res.status}`);
  }
  return Array.isArray(data) ? data : [];
}

export async function getOwnFeed(token) {
  const res = await fetch(`${API_BASE}/tweet/ownFeed`, {
    method: "GET",
    headers: withHeaders(token),
  });

  const data = await res.json().catch(() => null);
  if (!res.ok) throw new Error(data?.message || JSON.stringify(data) || `HTTP ${res.status}`);
  return Array.isArray(data) ? data : [];
}

export async function getTweetsByUserId(token, userId) {
  const res = await fetch(`${API_BASE}/tweet/findByUserId?userId=${encodeURIComponent(userId)}`, {
    method: "GET",
    headers: withHeaders(token),
  });

  const data = await res.json().catch(() => null);
  if (!res.ok) {
    throw new Error(data?.message || JSON.stringify(data) || `HTTP ${res.status}`);
  }
  return Array.isArray(data) ? data : [];
}

export async function getMe(token) {
  const res = await fetch(`${API_BASE}/user/me`, {
    method: "GET",
    headers: withHeaders(token),
  });

  const data = await res.json().catch(() => null);
  if (!res.ok) throw new Error(data?.message || JSON.stringify(data) || `HTTP ${res.status}`);
  return data;
}

export async function deleteTweet(token, tweetId) {
  const res = await fetch(`${API_BASE}/tweet/${encodeURIComponent(tweetId)}`, {
    method: "DELETE",
    headers: withHeaders(token),
  });
  const text = await res.text().catch(() => "");
  if (!res.ok) throw new Error(text || `HTTP ${res.status}`);
  return text;
}

export async function createTweet(token, content) {
  const res = await fetch(`${API_BASE}/tweet`, {
    method: "POST",
    headers: withHeaders(token),
    body: JSON.stringify({ content }),
  });

  const text = await res.text();
  if (!res.ok) throw new Error(text || (await parseError(res)));
  return text;
}

export async function likeTweet(token, tweetId) {
  const res = await fetch(`${API_BASE}/like?tweetId=${encodeURIComponent(tweetId)}`, {
    method: "POST",
    headers: withHeaders(token),
  });

  const text = await res.text();
  if (!res.ok) throw new Error(text || (await parseError(res)));
  return text;
}

export async function retweet(token, tweetId) {
  const res = await fetch(`${API_BASE}/retweet?tweetId=${encodeURIComponent(tweetId)}`, {
    method: "POST",
    headers: withHeaders(token),
  });

  const text = await res.text();
  if (!res.ok) throw new Error(text || (await parseError(res)));
  return text;
}

export async function commentOnTweet(token, tweetId, content) {
  const res = await fetch(`${API_BASE}/comment`, {
    method: "POST",
    headers: withHeaders(token),
    body: JSON.stringify({ tweetId, content }),
  });

  const text = await res.text();
  if (!res.ok) throw new Error(text || (await parseError(res)));
  return text;
}

export async function deleteComment(token, commentId) {
  const res = await fetch(`${API_BASE}/comment/${encodeURIComponent(commentId)}`, {
    method: "DELETE",
    headers: withHeaders(token),
  });

  const text = await res.text().catch(() => "");
  if (!res.ok) throw new Error(text || `HTTP ${res.status}`);
  return text;
}

export async function getCommentsByTweetId(token, tweetId) {
  const res = await fetch(`${API_BASE}/comment/findByTweetId?tweetId=${encodeURIComponent(tweetId)}`, {
    method: "GET",
    headers: withHeaders(token),
  });

  const data = await res.json().catch(() => null);
  if (!res.ok) {
    throw new Error(data?.message || JSON.stringify(data) || `HTTP ${res.status}`);
  }
  return Array.isArray(data) ? data : [];
}

export async function getLikeCount(token, tweetId) {
  const res = await fetch(`${API_BASE}/like/count?tweetId=${encodeURIComponent(tweetId)}`, {
    method: "GET",
    headers: withHeaders(token),
  });

  const data = await res.json().catch(() => null);
  if (!res.ok) throw new Error(data?.message || JSON.stringify(data) || `HTTP ${res.status}`);
  return typeof data === "number" ? data : Number(data) || 0;
}

export async function isLiked(token, tweetId) {
  const res = await fetch(`${API_BASE}/like/isLiked?tweetId=${encodeURIComponent(tweetId)}`, {
    method: "GET",
    headers: withHeaders(token),
  });

  const data = await res.json().catch(() => null);
  if (!res.ok) throw new Error(data?.message || JSON.stringify(data) || `HTTP ${res.status}`);
  return Boolean(data);
}

export async function dislikeTweet(token, tweetId) {
  const res = await fetch(`${API_BASE}/like/dislike?tweetId=${encodeURIComponent(tweetId)}`, {
    method: "POST",
    headers: withHeaders(token),
  });

  const text = await res.text().catch(() => "");
  if (!res.ok) throw new Error(text || `HTTP ${res.status}`);
  return text;
}

export async function getRetweetCount(token, tweetId) {
  const res = await fetch(`${API_BASE}/retweet/count?tweetId=${encodeURIComponent(tweetId)}`, {
    method: "GET",
    headers: withHeaders(token),
  });

  const data = await res.json().catch(() => null);
  if (!res.ok) throw new Error(data?.message || JSON.stringify(data) || `HTTP ${res.status}`);
  return typeof data === "number" ? data : Number(data) || 0;
}

export async function isRetweeted(token, tweetId) {
  const res = await fetch(`${API_BASE}/retweet/isRetweeted?tweetId=${encodeURIComponent(tweetId)}`, {
    method: "GET",
    headers: withHeaders(token),
  });

  const data = await res.json().catch(() => null);
  if (!res.ok) throw new Error(data?.message || JSON.stringify(data) || `HTTP ${res.status}`);
  return Boolean(data);
}

export async function deleteRetweet(token, tweetId) {
  const res = await fetch(`${API_BASE}/retweet/${encodeURIComponent(tweetId)}`, {
    method: "DELETE",
    headers: withHeaders(token),
  });

  const text = await res.text().catch(() => "");
  if (!res.ok) throw new Error(text || `HTTP ${res.status}`);
  return text;
}
