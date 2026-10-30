import test from 'node:test'
import assert from 'node:assert/strict'
import { api, ApiError, setAccessToken, getAccessToken, clearAccessToken } from '../src/api.ts'

test('cliente JWT envia JSON e Bearer, encerra sessão em 401 e preserva sessão em 403', async () => {
  const storage = new Map<string, string>()
  Object.defineProperty(globalThis, 'sessionStorage', { configurable: true, value: {
    getItem: (key: string) => storage.get(key) ?? null,
    setItem: (key: string, value: string) => storage.set(key, value),
    removeItem: (key: string) => storage.delete(key),
  } })
  const events = new EventTarget()
  Object.defineProperty(globalThis, 'window', { configurable: true, value: events })
  let expired = 0
  events.addEventListener('session-expired', () => expired++)
  const originalFetch = globalThis.fetch
  try {
    setAccessToken('test-token')
    globalThis.fetch = async (url, options) => {
      assert.equal(url, '/api/inventory/products')
      assert.equal(new Headers(options?.headers).get('Authorization'), 'Bearer test-token')
      assert.equal(new Headers(options?.headers).get('Content-Type'), 'application/json')
      assert.equal(options?.credentials, 'omit')
      return new Response('{"id":1}')
    }
    assert.deepEqual(await api('/inventory/products', { method: 'POST', body: '{}' }), { id: 1 })
    globalThis.fetch = async () => new Response('{}', { status: 403 })
    await assert.rejects(api('/admin/users'), (error: unknown) => error instanceof ApiError && error.status === 403)
    assert.equal(getAccessToken(), 'test-token')
    globalThis.fetch = async (_url, options) => {
      assert.equal(new Headers(options?.headers).has('Authorization'), false)
      return new Response('{}', { status: 401 })
    }
    await assert.rejects(api('/public/auth/login', { method: 'POST', body: '{}' }), ApiError)
    assert.equal(expired, 0)
    globalThis.fetch = async () => new Response('{}', { status: 401 })
    await assert.rejects(api('/auth/me'), ApiError)
    assert.equal(expired, 1)
    assert.equal(getAccessToken(), null)
    setAccessToken('logout-test')
    clearAccessToken()
    assert.equal(getAccessToken(), null)
  } finally {
    globalThis.fetch = originalFetch
  }
})
