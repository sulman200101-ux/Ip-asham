# GKE Expo App - التطبيق المتقدم

تطبيق React Native متقدم مع Expo و EAS Build

## 📋 المتطلبات
- Node.js 18+
- npm أو yarn
- Expo CLI

## 🚀 التثبيت والبدء السريع

```bash
# تثبيت المكتبات
npm install

# تشغيل التطبيق
npm start
```

## 📱 الأوامر الأساسية

```bash
# على iOS
npm run ios

# على Android
npm run android

# على الويب
npm run web

# تشغيل الاختبارات
npm test
```

## 🏗️ البناء عبر EAS

### بناء التطوير (Development)
```bash
eas build --platform ios --profile development
eas build --platform android --profile development
```

### بناء الإنتاج (Production)
```bash
eas build --platform ios --profile production
eas build --platform android --profile production
```

## 📤 النشر على Expo

```bash
npm start
# اضغط 'P' لنشر التطبيق على Expo
```

أو مباشرة:
```bash
expo publish
```

## 📁 هيكل المشروع

```
Ip-asham/
├── App.js              # الشاشة الرئيسية
├── app.json            # إعدادات Expo
├── eas.json            # إعدادات EAS Build
├── package.json        # المكتبات والسكريبتات
├── .github/workflows/  # CI/CD Automation
└── assets/             # الصور والأيقونات
```

## 🔐 إعدادات GitHub Secrets المطلوبة

أضف Tokens التالية إلى GitHub Secrets:
1. **EXPO_TOKEN** - للنشر التلقائي على Expo
2. **EAS_TOKEN** - لبناء التطبيق عبر EAS

## ✅ حالة الإعداد

- ✅ ملفات الإعداد الأساسية (app.json, eas.json, package.json)
- ✅ التطبيق الرئيسي (App.js)
- ✅ GitHub Actions Workflow للبناء والنشر التلقائي
- ✅ إعدادات iOS و Android

## 🔗 الروابط المهمة

- **Expo Dashboard**: https://expo.dev
- **EAS Documentation**: https://docs.expo.dev/eas
- **Repository**: https://github.com/sulman200101-ux/Ip-asham
- **Workflow**: https://github.com/sulman200101-ux/Ip-asham/actions

## 📝 الخطوات التالية

1. إضافة `EXPO_TOKEN` و `EAS_TOKEN` في GitHub Secrets
2. تخصيص الأيقونات والصور في مجلد assets/
3. تحديث معرف التطبيق في app.json
4. دفع التغييرات إلى main branch لتشغيل البناء التلقائي

---

جاهز للبناء والنشر! 🎉
