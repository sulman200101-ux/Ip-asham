import React from 'react';
import { StyleSheet, Text, View, SafeAreaView, StatusBar, TouchableOpacity } from 'react-native';

export default function App() {
  return (
    <SafeAreaView style={styles.container}>
      <StatusBar barStyle="light-content" backgroundColor="#0f172a" />
      <View style={styles.card}>
        <Text style={styles.title}>🚀 تطبيق التداول والأسهم</Text>
        <Text style={styles.subtitle}>GKE Trading App - جاهز للتشغيل</Text>
        <TouchableOpacity style={styles.button} onPress={() => alert('التطبيق يعمل بنجاح!')}>
          <Text style={styles.buttonText}>اضغط للتجربة</Text>
        </TouchableOpacity>
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#0f172a', justifyContent: 'center', alignItems: 'center', padding: 20 },
  card: { backgroundColor: '#1e293b', borderRadius: 16, padding: 24, width: '100%', alignItems: 'center' },
  title: { fontSize: 22, fontWeight: 'bold', color: '#38bdf8', marginBottom: 8 },
  subtitle: { fontSize: 14, color: '#94a3b8', marginBottom: 20 },
  button: { backgroundColor: '#2563eb', paddingVertical: 12, paddingHorizontal: 32, borderRadius: 10 },
  buttonText: { color: '#ffffff', fontSize: 16, fontWeight: 'bold' }
});
