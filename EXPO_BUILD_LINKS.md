# 🚀 رابط البناء على Expo

## روابط البناء الجاهزة:

### 1️⃣ **بناء التطوير (Development)**
```
eas build --platform ios --profile development
eas build --platform android --profile development
```

**الرابط التتبعي**: https://expo.dev/builds

---

### 2️⃣ **بناء الإنتاج (Production)**
```
eas build --platform ios --profile production
eas build --platform android --profile production
```

**الرابط التتبعي**: https://expo.dev/builds

---

### 3️⃣ **بناء عبر GitHub Actions التلقائي**

**رابط الـ Workflow**:
```
https://github.com/sulman200101-ux/Ip-asham/actions/workflows/google.yml
```

**خطوات البناء التلقائي**:
1. ادفع Commit إلى main
2. سيعمل Workflow تلقائياً
3. تابع على: https://github.com/sulman200101-ux/Ip-asham/actions

---

### 4️⃣ **نشر على Expo مباشرة**
```bash
npm start
# اضغط P في Terminal
# أو
expo publish
```

**الرابط**: https://expo.dev/@sulman200101/gke-expo-app

---

## 📊 روابط المراقبة والتتبع:

| الخدمة | الرابط |
|--------|--------|
| **Expo Dashboard** | https://expo.dev |
| **Expo Builds** | https://expo.dev/builds |
| **GitHub Actions** | https://github.com/sulman200101-ux/Ip-asham/actions |
| **GitHub Workflow** | https://github.com/sulman200101-ux/Ip-asham/actions/workflows/google.yml |
| **Repository** | https://github.com/sulman200101-ux/Ip-asham |

---

## ✅ متطلبات البناء:

- ✅ Tokens مضافة في GitHub Secrets (`EXPO_TOKEN` و `EAS_TOKEN`)
- ✅ ملفات الإعداد الكاملة (`app.json`, `eas.json`, `package.json`)
- ✅ GitHub Actions Workflow جاهز

---

## 🎯 خطوات البدء الفوري:

1. **التسجيل في Expo**: https://expo.dev
2. **إنشاء Tokens**: https://expo.dev/settings/tokens
3. **إضافة في GitHub**: https://github.com/sulman200101-ux/Ip-asham/settings/secrets/actions
4. **البدء بالبناء**: 
   ```bash
   eas build --platform ios --profile production
   ```

---

**جاهز للبناء! 🎉**
