// Pinia store for global state management
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAppStore = defineStore('app', () => {
  // Current user
  const currentUser = ref(null)
  const setCurrentUser = (user) => {
    currentUser.value = user
  }

  // Posts
  const posts = ref([])
  const setPosts = (newPosts) => {
    posts.value = newPosts
  }
  const addPost = (post) => {
    posts.value = [post, ...posts.value]
  }

  // Likes
  const likedPosts = ref(new Set())
  const toggleLike = (postId) => {
    const newLiked = new Set(likedPosts.value)
    if (newLiked.has(postId)) {
      newLiked.delete(postId)
    } else {
      newLiked.add(postId)
    }
    likedPosts.value = newLiked
  }

  // Follows
  const followingUsers = ref(new Set())
  const toggleFollow = (userId) => {
    const newFollowing = new Set(followingUsers.value)
    if (newFollowing.has(userId)) {
      newFollowing.delete(userId)
    } else {
      newFollowing.add(userId)
    }
    followingUsers.value = newFollowing
  }

  // Followers and Following
  const followers = ref(new Map())
  const setFollowers = (userId, followersList) => {
    const newFollowers = new Map(followers.value)
    newFollowers.set(userId, followersList)
    followers.value = newFollowers
  }

  const following = ref(new Map())
  const setFollowing = (userId, followingList) => {
    const newFollowing = new Map(following.value)
    newFollowing.set(userId, followingList)
    following.value = newFollowing
  }

  // Comments
  const comments = ref(new Map())
  const addComment = (postId, comment) => {
    const newComments = new Map(comments.value)
    if (!newComments.has(postId)) {
      newComments.set(postId, [])
    }
    newComments.get(postId).push(comment)
    comments.value = newComments
  }
  const getComments = (postId) => {
    return comments.value.get(postId) || []
  }

  // Travel logs
  const travelLogs = ref([])
  const setTravelLogs = (logs) => {
    travelLogs.value = logs
  }
  const addTravelLog = (log) => {
    travelLogs.value = [...travelLogs.value, log]
  }

  return {
    currentUser,
    setCurrentUser,
    posts,
    setPosts,
    addPost,
    likedPosts,
    toggleLike,
    followingUsers,
    toggleFollow,
    followers,
    setFollowers,
    following,
    setFollowing,
    comments,
    addComment,
    getComments,
    travelLogs,
    setTravelLogs,
    addTravelLog,
  }
})
