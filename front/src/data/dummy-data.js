// Dummy data for development and demo purposes

export const dummyUsers = {
  'user-1': {
    id: 'user-1',
    nickname: '박기택',
    profileImage: '/user-1.jpg',
    bio: '✈️ 여행 사진작가 | 세계를 탐험하는 중 🌍',
    followerCount: 234,
    followingCount: 145,
    createdAt: '2024-01-15',
  },
  'user-2': {
    id: 'user-2',
    nickname: '은태현',
    profileImage: '/user-2.jpg',
    bio: '🏔️ 등산 마니아 | 📸 사진 찍는 걸 좋아해요',
    followerCount: 567,
    followingCount: 298,
    createdAt: '2023-11-20',
  },
  'user-3': {
    id: 'user-3',
    nickname: '송주헌',
    profileImage: '/user-3.jpg',
    bio: '🌴 바다 좋아요 | 🎒 가성비 여행 팁 공유 | 🤝 소통해요!',
    followerCount: 189,
    followingCount: 256,
    createdAt: '2024-02-10',
  },
}

export const dummyPosts = [
  {
    id: 'post-1',
    userId: 'user-1',
    user: dummyUsers['user-1'],
    imageUrl: '/post-1.jpg',
    travelLocation: '우리집 앞',
    caption:
      '멋진 바이크에요 같이 달려볼까요👍',
    likeCount: 342,
    commentCount: 28,
    isLiked: false,
    createdAt: '2024-12-03T14:30:00',
  },
  {
    id: 'post-2',
    userId: 'user-2',
    user: dummyUsers['user-2'],
    imageUrl: '/snowy-mountain-peak-hiking-adventure.jpg',
    travelLocation: 'Swiss Alps, Switzerland',
    caption: '6시간 등산 끝에 정상 도착! ⛰️ 힘들었지만 정말 올라올 만한 가치가 있었어요.',
    likeCount: 521,
    commentCount: 45,
    isLiked: false,
    createdAt: '2024-12-01T09:15:00',
  },
  {
    id: 'post-3',
    userId: 'user-3',
    user: dummyUsers['user-3'],
    imageUrl: '/tropical-beach-palm-trees-ocean.jpg',
    travelLocation: 'Bali, Indonesia',
    caption: '진짜 천국 찾았어요 🏝️ 이런 아침을 맞이하는 게 꿈 아닌가요?',
    likeCount: 289,
    commentCount: 35,
    isLiked: false,
    createdAt: '2024-11-30T16:45:00',
  },
]

export const dummyComments = {
  'post-1': [
    {
      id: 'comment-1',
      postId: 'post-1',
      userId: 'user-2',
      user: dummyUsers['user-2'],
      content: '진짜 멋지네요! 버킷리스트에 추가합니다 🎯',
      createdAt: '2024-12-03T15:20:00',
    },
    {
      id: 'comment-2',
      postId: 'post-1',
      userId: 'user-3',
      user: dummyUsers['user-3'],
      content: '라이더 박기택 멋져요!',
      createdAt: '2024-12-03T16:10:00',
    },
  ],
  'post-2': [
    {
      id: 'comment-3',
      postId: 'post-2',
      userId: 'user-1',
      user: dummyUsers['user-1'],
      content: '간지납니다 🙌',
      createdAt: '2024-12-01T11:30:00',
    },
  ],
}

export const dummyFollowers = {
  'user-1': [dummyUsers['user-2'], dummyUsers['user-3']],
  'user-2': [dummyUsers['user-1'], dummyUsers['user-3']],
  'user-3': [dummyUsers['user-1'], dummyUsers['user-2']],
}

export const dummyFollowing = {
  'user-1': [dummyUsers['user-2']],
  'user-2': [dummyUsers['user-1'], dummyUsers['user-3']],
  'user-3': [dummyUsers['user-1']],
}

export const userPostsMap = {
  'user-1': [dummyPosts[0]],
  'user-2': [dummyPosts[1]],
  'user-3': [dummyPosts[2]],
}

export const dummyTravelLogs = [
  {
    id: 'travel-1',
    userId: 'user-1',
    latitude: 36.7372,
    longitude: 25.4615,
    locationName: 'Santorini Caldera',
    date: '2024-12-03',
    description:
      '산토리니 칼데라에서 가장 멋진 일몰을 봤어요. 푸른 바다를 배경으로 한 하얀 건물들이 정말 환상적이었어요.',
    photos: ['/placeholder.svg?key=bgzwr'],
    createdAt: '2024-12-03T14:30:00',
  },
  {
    id: 'travel-2',
    userId: 'user-2',
    latitude: 46.8182,
    longitude: 8.2275,
    locationName: 'Jungfrau Summit',
    date: '2024-12-01',
    description: '융프라우 정상 도착! 해발 4158m에서 본 경치가 정말 환상적이었어요. 정말 올라올 만했어요!',
    photos: ['/placeholder.svg?key=picbp'],
    createdAt: '2024-12-01T09:15:00',
  },
  {
    id: 'travel-3',
    userId: 'user-3',
    latitude: -8.6705,
    longitude: 115.2126,
    locationName: 'Bali Beach Resort',
    date: '2024-11-30',
    description: '지상낙원이 따로 없어요! 투명한 물과 하얀 모래사장이 끝없이 펼쳐져 있어요.',
    photos: ['/placeholder.svg?key=bllbx'],
    createdAt: '2024-11-30T16:45:00',
  },
]

export const dummyNotifications = [
  {
    id: 'notif-1',
    type: 'like',
    userId: 'user-2',
    user: dummyUsers['user-2'],
    postId: 'post-1',
    post: dummyPosts[0],
    message: '님이 회원님의 게시물을 좋아합니다.',
    isRead: false,
    createdAt: '2024-12-06T10:30:00',
  },
  {
    id: 'notif-2',
    type: 'comment',
    userId: 'user-3',
    user: dummyUsers['user-3'],
    postId: 'post-1',
    post: dummyPosts[0],
    commentText: '라이더 박기택 멋져요!',
    message: '님이 회원님의 게시물에 댓글을 남겼습니다:',
    isRead: false,
    createdAt: '2024-12-05T15:20:00',
  },
  {
    id: 'notif-3',
    type: 'follow',
    userId: 'user-3',
    user: dummyUsers['user-3'],
    message: '님이 회원님을 팔로우하기 시작했습니다.',
    isRead: false,
    createdAt: '2024-12-04T09:15:00',
  },
  {
    id: 'notif-4',
    type: 'like',
    userId: 'user-2',
    user: dummyUsers['user-2'],
    postId: 'post-1',
    post: dummyPosts[0],
    message: '님이 회원님의 게시물을 좋아합니다.',
    isRead: true,
    createdAt: '2024-12-03T14:45:00',
  },
  {
    id: 'notif-5',
    type: 'comment',
    userId: 'user-2',
    user: dummyUsers['user-2'],
    postId: 'post-1',
    post: dummyPosts[0],
    commentText: '진짜 멋지네요! 버킷리스트에 추가합니다 🎯',
    message: '님이 회원님의 게시물에 댓글을 남겼습니다:',
    isRead: true,
    createdAt: '2024-12-03T15:20:00',
  },
]
