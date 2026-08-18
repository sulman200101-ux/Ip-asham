# 🚀 GKE Expo React Native App

تطبيق React Native يتم بناؤه ونشره عبر Expo - يعمل على iOS و Android و Web

## البدء السريع

### التثبيت
```bash
npm install
```

### التشغيل

#### على جهازك المحلي
```bash
# تشغيل عام (Expo Go)
npm start

# تشغيل على iOS
npm run ios

# تشغيل على Android
npm run android

# تشغيل على Web
npm run web
```

#### المسح بواسطة Expo Go
امسح رمز QR الذي يظهر في الترمينال باستخدام:
- **iOS**: تطبيق Camera
- **Android**: تطبيق Expo Go

## البناء والنشر

### بناء للإنتاج

```bash
# بناء iOS
npm run build:ios

# بناء Android
npm run build:android
```

### النشر على App Store و Google Play

```bash
# نشر iOS
npm run submit:ios

# ن��ر Android
npm run submit:android
```

## هيكل المشروع

```
.
├── App.tsx                  # الشاشة الرئيسية
├── index.js                 # نقطة الدخول
├── app.json                 # إعدادات Expo
├── .github/
│   └── workflows/
│       └── expo.yml         # CI/CD workflow
├── assets/                  # الصور والأيقونات
├── package.json             # المكتبات
└── tsconfig.json            # إعدادات TypeScript
```

## المتطلبات

- Node.js 18+
- npm أو yarn
- Expo CLI: `npm install -g expo-cli`
- Xcode (لـ iOS)
- Android Studio (لـ Android)

## المميزات

✅ **Cross-platform**: iOS, Android, Web في تطبيق واحد
✅ **Hot Reload**: التطوير السريع مع إعادة تحميل فورية
✅ **Expo Go**: اختبار فوري بدون بناء
✅ **Push Notifications**: دعم الإشعارات
✅ **Auto Updates**: تحديثات تلقائية عبر Expo
✅ **EAS Build**: بناء سحابي احترافي

## CI/CD

الـ GitHub Actions workflow في `.github/workflows/expo.yml` يقوم بـ:
1. بناء التطبيق مع EAS Build
2. نشره على iOS و Android App Stores تلقائياً
3. نشره على Expo Updates

### المتغيرات المطلوبة

أضف إلى GitHub Secrets:
- `EXPO_TOKEN`: من https://expo.dev/settings/tokens
- `EAS_TOKEN`: من https://eas.build/

## الروابط المهمة

- 🔗 [Expo Dashboard](https://expo.dev/)
- 🔗 [EAS Build](https://eas.build/)
- 🔗 [React Native Docs](https://reactnative.dev/)
- 🔗 [Expo Docs](https://docs.expo.dev/)

## الحصول على التوكنات

### Expo Token
1. اذهب إلى https://expo.dev/settings/tokens
2. أنشئ token جديد
3. انسخه وأضفه إلى GitHub Secrets كـ `EXPO_TOKEN`

### EAS Token
1. اذهب إلى https://eas.build/
2. سجل دخول بحسابك
3. انسخ الـ token وأضفه كـ `EAS_TOKEN`

## تطوير

### إضافة مكتبات
```bash
npm install <package>
# أو
expo install <package>
```

### إضافة صور/أيقونات
ضع الصور في مجلد `assets/`:
- `icon.png` - أيقونة التطبيق
- `splash.png` - شاشة البداية
- `adaptive-icon.png` - أيقونة Android
- `favicon.png` - أيقونة الويب

## المسح والاختبار

```bash
# مسح بيانات الكاش
expo start -c

# اختبار محدد
npm test
```

## استكشاف الأخطاء

### المشكلة: لا يعمل الـ QR code
```bash
# جرب
expo start -c
```

### المشكلة: خطأ في البناء
```bash
# تحديث الـ EAS CLI
npm install -g eas-cli@latest
```

## التراخيص

MIT

---

**البدء الآن:**
```bash
npm install && npm start
```

امسح الـ QR code وابدأ التطوير! 🎉
