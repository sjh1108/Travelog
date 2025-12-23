import axios from 'axios'

const API_BASE_URL = '/api'

// 사용자 API
export const userAPI = {
  // 회원가입
  register: async (userData) => {
    const response = await axios.post(`${API_BASE_URL}/users/join`, userData)
    return response.data
  },

  // 로그인
  login: async (credentials) => {
    const response = await axios.post(`${API_BASE_URL}/users/login`, credentials)
    return response.data
  },

  // 회원정보 수정
  updateProfile: async (userData) => {
    const response = await axios.put(`${API_BASE_URL}/users/me`, userData)
    return response.data
  }
}

// 게시물 API
export const postAPI = {
  // 전체 게시물 조회
  getPosts: async () => {
    const response = await axios.get(`${API_BASE_URL}/posts`)
    return response.data
  },

  // 게시물 작성
  createPost: async (postData) => {
    const response = await axios.post(`${API_BASE_URL}/posts`, postData)
    return response.data
  }
}

// 댓글 API
export const commentAPI = {
  // 댓글 목록 조회
  getComments: async (postId) => {
    const response = await axios.get(`${API_BASE_URL}/comments/${postId}`)
    return response.data
  },

  // 댓글 작성
  createComment: async (commentData) => {
    const response = await axios.post(`${API_BASE_URL}/comments`, commentData)
    return response.data
  },

  // 댓글 삭제
  deleteComment: async (commentId) => {
    const response = await axios.delete(`${API_BASE_URL}/comments/${commentId}`)
    return response.data
  }
}

// 좋아요 API
export const likeAPI = {
  // 좋아요 토글
  toggleLike: async (postId) => {
    const response = await axios.post(`${API_BASE_URL}/likes/${postId}`)
    return response.data
  }
}

// 여행 기록 API
export const travelAPI = {
  // 전체 여행 기록 조회
  getTravels: async () => {
    const response = await axios.get(`${API_BASE_URL}/travels`)
    return response.data
  },

  // 내 여행 기록 조회
  getMyTravels: async () => {
    const response = await axios.get(`${API_BASE_URL}/travels/my`)
    return response.data
  },

  // 여행 기록 작성
  createTravel: async (travelData) => {
    const response = await axios.post(`${API_BASE_URL}/travels`, travelData)
    return response.data
  },

  // 여행 기록 삭제
  deleteTravel: async (travelId) => {
    const response = await axios.delete(`${API_BASE_URL}/travels/${travelId}`)
    return response.data
  },

  // 상세 일정 조회
  getTravelDetails: async (travelId) => {
    const response = await axios.get(`${API_BASE_URL}/travels/${travelId}/details`)
    return response.data
  },

  // 상세 일정 추가
  createTravelDetails: async (travelId, detailsData) => {
    const response = await axios.post(`${API_BASE_URL}/travels/${travelId}/details`, detailsData)
    return response.data
  },

  // 상세 일정 삭제
  deleteTravelDetail: async (travelId, detailId) => {
    const response = await axios.delete(`${API_BASE_URL}/travels/${travelId}/details/${detailId}`)
    return response.data
  }
}

// 파일 업로드 API
export const fileAPI = {
  // 이미지 업로드 (단일)
  uploadImage: async (file) => {
    const formData = new FormData()
    formData.append('file', file)

    const response = await axios.post(`${API_BASE_URL}/files/upload`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
    return response.data
  },

  // 여러 이미지 업로드
  uploadMultipleImages: async (files) => {
    const formData = new FormData()
    files.forEach((file) => {
      formData.append('files', file)
    })

    const response = await axios.post(`${API_BASE_URL}/files/upload-multiple`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
    return response.data
  }
}

// 알림 API
export const notificationAPI = {
  // 내 알림 목록 조회
  getNotifications: async () => {
    const response = await axios.get(`${API_BASE_URL}/notifications`)
    return response.data
  },

  // 읽지 않은 알림 개수 조회
  getUnreadCount: async () => {
    const response = await axios.get(`${API_BASE_URL}/notifications/unread-count`)
    return response.data
  },

  // 알림 읽음 처리
  markAsRead: async (notificationId) => {
    const response = await axios.patch(`${API_BASE_URL}/notifications/${notificationId}/read`)
    return response.data
  },

  // 알림 전체 읽음 처리
  markAllAsRead: async () => {
    const response = await axios.patch(`${API_BASE_URL}/notifications/read-all`)
    return response.data
  },

  // 알림 삭제
  deleteNotification: async (notificationId) => {
    const response = await axios.delete(`${API_BASE_URL}/notifications/${notificationId}`)
    return response.data
  }
}
