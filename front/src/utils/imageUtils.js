/**
 * 이미지 URL을 전체 경로로 변환
 * @param {string} imageUrl - 원본 이미지 URL
 * @returns {string} - 전체 이미지 URL
 */
export function getFullImageUrl(imageUrl) {
  if (!imageUrl) {
    return '/placeholder.svg'
  }

  // 이미 전체 URL인 경우 (http:// 또는 https://로 시작)
  if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) {
    return imageUrl
  }

  // blob: URL (로컬 프리뷰)
  if (imageUrl.startsWith('blob:')) {
    return imageUrl
  }

  // 상대 경로인 경우 백엔드 서버 URL 추가
  // /images/... 형태로 오는 경우
  if (imageUrl.startsWith('/images/')) {
    return `http://localhost:8080${imageUrl}`
  }

  // images/... 형태로 오는 경우
  if (imageUrl.startsWith('images/')) {
    return `http://localhost:8080/${imageUrl}`
  }

  // 기타 상대 경로
  return `http://localhost:8080/images/${imageUrl}`
}

/**
 * 프로필 이미지 URL을 전체 경로로 변환 (기본 프로필 이미지 포함)
 * @param {string} imageUrl - 원본 이미지 URL
 * @returns {string} - 전체 이미지 URL
 */
export function getProfileImageUrl(imageUrl) {
  // 빈 값이거나 placeholder인 경우 기본 프로필 이미지 반환
  if (!imageUrl || imageUrl === '/placeholder.svg' || imageUrl === 'placeholder.svg') {
    return '/default-profile.svg'
  }

  // 일반 이미지 URL 변환 로직 재사용
  return getFullImageUrl(imageUrl)
}
