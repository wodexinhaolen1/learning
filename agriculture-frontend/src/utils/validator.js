// 密码验证规则：至少6位，必须包含数字和英文字母
export const validatePassword = (password) => {
  const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{6,}$/
  return passwordRegex.test(password)
}

// 密码验证错误提示
export const PASSWORD_RULE_MESSAGE = '密码必须至少6位，且包含数字和英文字母' 