const DEFAULT_API_BASE = "http://localhost:8080";

const apiBase = (() => {
  if (process.env.REACT_APP_API_BASE) {
    return process.env.REACT_APP_API_BASE;
  }

  if (process.env.NODE_ENV === "development") {
    // 走 CRA dev-server proxy，使用相对路径避免 CORS
    return "";
  }

  return DEFAULT_API_BASE;
})();

let authTokenProvider = null;
let unauthorizedHandler = null;

export const API_BASE_URL = apiBase;

export const setAuthTokenProvider = (provider) => {
  authTokenProvider = provider;
};

export const setUnauthorizedHandler = (handler) => {
  unauthorizedHandler = handler;
};

const buildHeaders = (body, extraHeaders = {}, auth = true) => {
  const headers = { ...extraHeaders };
  const isFormData = body instanceof FormData;

  if (!isFormData && body !== undefined && !headers["Content-Type"]) {
    headers["Content-Type"] = "application/json";
  }

  if (auth && authTokenProvider) {
    const token = authTokenProvider();
    if (token) {
      headers.Authorization = `Bearer ${token}`;
    }
  }

  return headers;
};

const parseErrorMessage = async (response) => {
  const contentType = response.headers.get("content-type") || "";
  try {
    if (contentType.includes("application/json")) {
      const data = await response.json();
      if (data && data.message) {
        return data.message;
      }
      return JSON.stringify(data);
    }

    const text = await response.text();
    if (text) {
      return text;
    }
  } catch (error) {
    // ignore
  }

  return `Request failed with status ${response.status}`;
};

export const request = async (
  path,
  { method = "GET", body, headers = {}, auth = true } = {}
) => {
  const requestOptions = {
    method,
    headers: buildHeaders(body, headers, auth),
  };

  if (body !== undefined) {
    if (body instanceof FormData) {
      requestOptions.body = body;
    } else if (typeof body === "string") {
      requestOptions.body = body;
    } else {
      requestOptions.body = JSON.stringify(body);
    }
  }

  const response = await fetch(`${apiBase}${path}`, requestOptions);

  if (response.status === 401 && unauthorizedHandler) {
    unauthorizedHandler();
  }

  if (!response.ok) {
    const message = await parseErrorMessage(response);
    const error = new Error(message);
    error.status = response.status;
    throw error;
  }

  if (response.status === 204) {
    return null;
  }

  const contentType = response.headers.get("content-type") || "";
  if (contentType.includes("application/json")) {
    return response.json();
  }

  return response.text();
};

export default request;
