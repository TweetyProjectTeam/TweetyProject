var Fu = Object.defineProperty;
var ju = (t, e, n) => e in t ? Fu(t, e, { enumerable: !0, configurable: !0, writable: !0, value: n }) : t[e] = n;
var yt = (t, e, n) => ju(t, typeof e != "symbol" ? e + "" : e, n);
/**
* @vue/shared v3.4.21
* (c) 2018-present Yuxi (Evan) You and Vue contributors
* @license MIT
**/
function Yi(t, e) {
  const n = new Set(t.split(","));
  return (r) => n.has(r);
}
const bt = {}, un = [], ie = () => {
}, Bu = () => !1, qr = (t) => t.charCodeAt(0) === 111 && t.charCodeAt(1) === 110 && // uppercase letter
(t.charCodeAt(2) > 122 || t.charCodeAt(2) < 97), Ji = (t) => t.startsWith("onUpdate:"), Tt = Object.assign, Qi = (t, e) => {
  const n = t.indexOf(e);
  n > -1 && t.splice(n, 1);
}, zu = Object.prototype.hasOwnProperty, ft = (t, e) => zu.call(t, e), rt = Array.isArray, cn = (t) => Gr(t) === "[object Map]", Uo = (t) => Gr(t) === "[object Set]", ut = (t) => typeof t == "function", Ct = (t) => typeof t == "string", _n = (t) => typeof t == "symbol", St = (t) => t !== null && typeof t == "object", Wo = (t) => (St(t) || ut(t)) && ut(t.then) && ut(t.catch), Ko = Object.prototype.toString, Gr = (t) => Ko.call(t), Du = (t) => Gr(t).slice(8, -1), Xo = (t) => Gr(t) === "[object Object]", Zi = (t) => Ct(t) && t !== "NaN" && t[0] !== "-" && "" + parseInt(t, 10) === t, Cn = /* @__PURE__ */ Yi(
  // the leading comma is intentional so empty string "" is also included
  ",key,ref,ref_for,ref_key,onVnodeBeforeMount,onVnodeMounted,onVnodeBeforeUpdate,onVnodeUpdated,onVnodeBeforeUnmount,onVnodeUnmounted"
), Hr = (t) => {
  const e = /* @__PURE__ */ Object.create(null);
  return (n) => e[n] || (e[n] = t(n));
}, Vu = /-(\w)/g, ke = Hr((t) => t.replace(Vu, (e, n) => n ? n.toUpperCase() : "")), qu = /\B([A-Z])/g, ue = Hr(
  (t) => t.replace(qu, "-$1").toLowerCase()
), Yo = Hr((t) => t.charAt(0).toUpperCase() + t.slice(1)), li = Hr((t) => t ? `on${Yo(t)}` : ""), ze = (t, e) => !Object.is(t, e), ui = (t, e) => {
  for (let n = 0; n < t.length; n++)
    t[n](e);
}, Nr = (t, e, n) => {
  Object.defineProperty(t, e, {
    configurable: !0,
    enumerable: !1,
    value: n
  });
}, Gu = (t) => {
  const e = parseFloat(t);
  return isNaN(e) ? t : e;
}, $s = (t) => {
  const e = Ct(t) ? Number(t) : NaN;
  return isNaN(e) ? t : e;
};
let Os;
const Jo = () => Os || (Os = typeof globalThis < "u" ? globalThis : typeof self < "u" ? self : typeof window < "u" ? window : typeof global < "u" ? global : {});
function ts(t) {
  if (rt(t)) {
    const e = {};
    for (let n = 0; n < t.length; n++) {
      const r = t[n], i = Ct(r) ? Ku(r) : ts(r);
      if (i)
        for (const s in i)
          e[s] = i[s];
    }
    return e;
  } else if (Ct(t) || St(t))
    return t;
}
const Hu = /;(?![^(]*\))/g, Uu = /:([^]+)/, Wu = /\/\*[^]*?\*\//g;
function Ku(t) {
  const e = {};
  return t.replace(Wu, "").split(Hu).forEach((n) => {
    if (n) {
      const r = n.split(Uu);
      r.length > 1 && (e[r[0].trim()] = r[1].trim());
    }
  }), e;
}
function es(t) {
  let e = "";
  if (Ct(t))
    e = t;
  else if (rt(t))
    for (let n = 0; n < t.length; n++) {
      const r = es(t[n]);
      r && (e += r + " ");
    }
  else if (St(t))
    for (const n in t)
      t[n] && (e += n + " ");
  return e.trim();
}
const Xu = "itemscope,allowfullscreen,formnovalidate,ismap,nomodule,novalidate,readonly", Yu = /* @__PURE__ */ Yi(Xu);
function Qo(t) {
  return !!t || t === "";
}
const Te = (t) => Ct(t) ? t : t == null ? "" : rt(t) || St(t) && (t.toString === Ko || !ut(t.toString)) ? JSON.stringify(t, Zo, 2) : String(t), Zo = (t, e) => e && e.__v_isRef ? Zo(t, e.value) : cn(e) ? {
  [`Map(${e.size})`]: [...e.entries()].reduce(
    (n, [r, i], s) => (n[ci(r, s) + " =>"] = i, n),
    {}
  )
} : Uo(e) ? {
  [`Set(${e.size})`]: [...e.values()].map((n) => ci(n))
} : _n(e) ? ci(e) : St(e) && !rt(e) && !Xo(e) ? String(e) : e, ci = (t, e = "") => {
  var n;
  return _n(t) ? `Symbol(${(n = t.description) != null ? n : e})` : t;
};
/**
* @vue/reactivity v3.4.21
* (c) 2018-present Yuxi (Evan) You and Vue contributors
* @license MIT
**/
let se;
class Ju {
  constructor(e = !1) {
    this.detached = e, this._active = !0, this.effects = [], this.cleanups = [], this.parent = se, !e && se && (this.index = (se.scopes || (se.scopes = [])).push(
      this
    ) - 1);
  }
  get active() {
    return this._active;
  }
  run(e) {
    if (this._active) {
      const n = se;
      try {
        return se = this, e();
      } finally {
        se = n;
      }
    }
  }
  /**
   * This should only be called on non-detached scopes
   * @internal
   */
  on() {
    se = this;
  }
  /**
   * This should only be called on non-detached scopes
   * @internal
   */
  off() {
    se = this.parent;
  }
  stop(e) {
    if (this._active) {
      let n, r;
      for (n = 0, r = this.effects.length; n < r; n++)
        this.effects[n].stop();
      for (n = 0, r = this.cleanups.length; n < r; n++)
        this.cleanups[n]();
      if (this.scopes)
        for (n = 0, r = this.scopes.length; n < r; n++)
          this.scopes[n].stop(!0);
      if (!this.detached && this.parent && !e) {
        const i = this.parent.scopes.pop();
        i && i !== this && (this.parent.scopes[this.index] = i, i.index = this.index);
      }
      this.parent = void 0, this._active = !1;
    }
  }
}
function Qu(t, e = se) {
  e && e.active && e.effects.push(t);
}
function Zu() {
  return se;
}
let Ye;
class ns {
  constructor(e, n, r, i) {
    this.fn = e, this.trigger = n, this.scheduler = r, this.active = !0, this.deps = [], this._dirtyLevel = 4, this._trackId = 0, this._runnings = 0, this._shouldSchedule = !1, this._depsLength = 0, Qu(this, i);
  }
  get dirty() {
    if (this._dirtyLevel === 2 || this._dirtyLevel === 3) {
      this._dirtyLevel = 1, en();
      for (let e = 0; e < this._depsLength; e++) {
        const n = this.deps[e];
        if (n.computed && (tc(n.computed), this._dirtyLevel >= 4))
          break;
      }
      this._dirtyLevel === 1 && (this._dirtyLevel = 0), nn();
    }
    return this._dirtyLevel >= 4;
  }
  set dirty(e) {
    this._dirtyLevel = e ? 4 : 0;
  }
  run() {
    if (this._dirtyLevel = 0, !this.active)
      return this.fn();
    let e = je, n = Ye;
    try {
      return je = !0, Ye = this, this._runnings++, As(this), this.fn();
    } finally {
      Fs(this), this._runnings--, Ye = n, je = e;
    }
  }
  stop() {
    var e;
    this.active && (As(this), Fs(this), (e = this.onStop) == null || e.call(this), this.active = !1);
  }
}
function tc(t) {
  return t.value;
}
function As(t) {
  t._trackId++, t._depsLength = 0;
}
function Fs(t) {
  if (t.deps.length > t._depsLength) {
    for (let e = t._depsLength; e < t.deps.length; e++)
      tl(t.deps[e], t);
    t.deps.length = t._depsLength;
  }
}
function tl(t, e) {
  const n = t.get(e);
  n !== void 0 && e._trackId !== n && (t.delete(e), t.size === 0 && t.cleanup());
}
let je = !0, Mi = 0;
const el = [];
function en() {
  el.push(je), je = !1;
}
function nn() {
  const t = el.pop();
  je = t === void 0 ? !0 : t;
}
function rs() {
  Mi++;
}
function is() {
  for (Mi--; !Mi && Ni.length; )
    Ni.shift()();
}
function nl(t, e, n) {
  if (e.get(t) !== t._trackId) {
    e.set(t, t._trackId);
    const r = t.deps[t._depsLength];
    r !== e ? (r && tl(r, t), t.deps[t._depsLength++] = e) : t._depsLength++;
  }
}
const Ni = [];
function rl(t, e, n) {
  rs();
  for (const r of t.keys()) {
    let i;
    r._dirtyLevel < e && (i ?? (i = t.get(r) === r._trackId)) && (r._shouldSchedule || (r._shouldSchedule = r._dirtyLevel === 0), r._dirtyLevel = e), r._shouldSchedule && (i ?? (i = t.get(r) === r._trackId)) && (r.trigger(), (!r._runnings || r.allowRecurse) && r._dirtyLevel !== 2 && (r._shouldSchedule = !1, r.scheduler && Ni.push(r.scheduler)));
  }
  is();
}
const il = (t, e) => {
  const n = /* @__PURE__ */ new Map();
  return n.cleanup = t, n.computed = e, n;
}, Ri = /* @__PURE__ */ new WeakMap(), Je = Symbol(""), Ti = Symbol("");
function Wt(t, e, n) {
  if (je && Ye) {
    let r = Ri.get(t);
    r || Ri.set(t, r = /* @__PURE__ */ new Map());
    let i = r.get(n);
    i || r.set(n, i = il(() => r.delete(n))), nl(
      Ye,
      i
    );
  }
}
function Me(t, e, n, r, i, s) {
  const o = Ri.get(t);
  if (!o)
    return;
  let l = [];
  if (e === "clear")
    l = [...o.values()];
  else if (n === "length" && rt(t)) {
    const u = Number(r);
    o.forEach((c, a) => {
      (a === "length" || !_n(a) && a >= u) && l.push(c);
    });
  } else
    switch (n !== void 0 && l.push(o.get(n)), e) {
      case "add":
        rt(t) ? Zi(n) && l.push(o.get("length")) : (l.push(o.get(Je)), cn(t) && l.push(o.get(Ti)));
        break;
      case "delete":
        rt(t) || (l.push(o.get(Je)), cn(t) && l.push(o.get(Ti)));
        break;
      case "set":
        cn(t) && l.push(o.get(Je));
        break;
    }
  rs();
  for (const u of l)
    u && rl(
      u,
      4
    );
  is();
}
const ec = /* @__PURE__ */ Yi("__proto__,__v_isRef,__isVue"), sl = new Set(
  /* @__PURE__ */ Object.getOwnPropertyNames(Symbol).filter((t) => t !== "arguments" && t !== "caller").map((t) => Symbol[t]).filter(_n)
), js = /* @__PURE__ */ nc();
function nc() {
  const t = {};
  return ["includes", "indexOf", "lastIndexOf"].forEach((e) => {
    t[e] = function(...n) {
      const r = dt(this);
      for (let s = 0, o = this.length; s < o; s++)
        Wt(r, "get", s + "");
      const i = r[e](...n);
      return i === -1 || i === !1 ? r[e](...n.map(dt)) : i;
    };
  }), ["push", "pop", "shift", "unshift", "splice"].forEach((e) => {
    t[e] = function(...n) {
      en(), rs();
      const r = dt(this)[e].apply(this, n);
      return is(), nn(), r;
    };
  }), t;
}
function rc(t) {
  const e = dt(this);
  return Wt(e, "has", t), e.hasOwnProperty(t);
}
class ol {
  constructor(e = !1, n = !1) {
    this._isReadonly = e, this._isShallow = n;
  }
  get(e, n, r) {
    const i = this._isReadonly, s = this._isShallow;
    if (n === "__v_isReactive")
      return !i;
    if (n === "__v_isReadonly")
      return i;
    if (n === "__v_isShallow")
      return s;
    if (n === "__v_raw")
      return r === (i ? s ? mc : al : s ? cl : ul).get(e) || // receiver is not the reactive proxy, but has the same prototype
      // this means the reciever is a user proxy of the reactive proxy
      Object.getPrototypeOf(e) === Object.getPrototypeOf(r) ? e : void 0;
    const o = rt(e);
    if (!i) {
      if (o && ft(js, n))
        return Reflect.get(js, n, r);
      if (n === "hasOwnProperty")
        return rc;
    }
    const l = Reflect.get(e, n, r);
    return (_n(n) ? sl.has(n) : ec(n)) || (i || Wt(e, "get", n), s) ? l : Kt(l) ? o && Zi(n) ? l : l.value : St(l) ? i ? fl(l) : Wr(l) : l;
  }
}
class ll extends ol {
  constructor(e = !1) {
    super(!1, e);
  }
  set(e, n, r, i) {
    let s = e[n];
    if (!this._isShallow) {
      const u = gn(s);
      if (!Rr(r) && !gn(r) && (s = dt(s), r = dt(r)), !rt(e) && Kt(s) && !Kt(r))
        return u ? !1 : (s.value = r, !0);
    }
    const o = rt(e) && Zi(n) ? Number(n) < e.length : ft(e, n), l = Reflect.set(e, n, r, i);
    return e === dt(i) && (o ? ze(r, s) && Me(e, "set", n, r) : Me(e, "add", n, r)), l;
  }
  deleteProperty(e, n) {
    const r = ft(e, n);
    e[n];
    const i = Reflect.deleteProperty(e, n);
    return i && r && Me(e, "delete", n, void 0), i;
  }
  has(e, n) {
    const r = Reflect.has(e, n);
    return (!_n(n) || !sl.has(n)) && Wt(e, "has", n), r;
  }
  ownKeys(e) {
    return Wt(
      e,
      "iterate",
      rt(e) ? "length" : Je
    ), Reflect.ownKeys(e);
  }
}
class ic extends ol {
  constructor(e = !1) {
    super(!0, e);
  }
  set(e, n) {
    return !0;
  }
  deleteProperty(e, n) {
    return !0;
  }
}
const sc = /* @__PURE__ */ new ll(), oc = /* @__PURE__ */ new ic(), lc = /* @__PURE__ */ new ll(
  !0
), ss = (t) => t, Ur = (t) => Reflect.getPrototypeOf(t);
function tr(t, e, n = !1, r = !1) {
  t = t.__v_raw;
  const i = dt(t), s = dt(e);
  n || (ze(e, s) && Wt(i, "get", e), Wt(i, "get", s));
  const { has: o } = Ur(i), l = r ? ss : n ? us : An;
  if (o.call(i, e))
    return l(t.get(e));
  if (o.call(i, s))
    return l(t.get(s));
  t !== i && t.get(e);
}
function er(t, e = !1) {
  const n = this.__v_raw, r = dt(n), i = dt(t);
  return e || (ze(t, i) && Wt(r, "has", t), Wt(r, "has", i)), t === i ? n.has(t) : n.has(t) || n.has(i);
}
function nr(t, e = !1) {
  return t = t.__v_raw, !e && Wt(dt(t), "iterate", Je), Reflect.get(t, "size", t);
}
function Bs(t) {
  t = dt(t);
  const e = dt(this);
  return Ur(e).has.call(e, t) || (e.add(t), Me(e, "add", t, t)), this;
}
function zs(t, e) {
  e = dt(e);
  const n = dt(this), { has: r, get: i } = Ur(n);
  let s = r.call(n, t);
  s || (t = dt(t), s = r.call(n, t));
  const o = i.call(n, t);
  return n.set(t, e), s ? ze(e, o) && Me(n, "set", t, e) : Me(n, "add", t, e), this;
}
function Ds(t) {
  const e = dt(this), { has: n, get: r } = Ur(e);
  let i = n.call(e, t);
  i || (t = dt(t), i = n.call(e, t)), r && r.call(e, t);
  const s = e.delete(t);
  return i && Me(e, "delete", t, void 0), s;
}
function Vs() {
  const t = dt(this), e = t.size !== 0, n = t.clear();
  return e && Me(t, "clear", void 0, void 0), n;
}
function rr(t, e) {
  return function(r, i) {
    const s = this, o = s.__v_raw, l = dt(o), u = e ? ss : t ? us : An;
    return !t && Wt(l, "iterate", Je), o.forEach((c, a) => r.call(i, u(c), u(a), s));
  };
}
function ir(t, e, n) {
  return function(...r) {
    const i = this.__v_raw, s = dt(i), o = cn(s), l = t === "entries" || t === Symbol.iterator && o, u = t === "keys" && o, c = i[t](...r), a = n ? ss : e ? us : An;
    return !e && Wt(
      s,
      "iterate",
      u ? Ti : Je
    ), {
      // iterator protocol
      next() {
        const { value: f, done: h } = c.next();
        return h ? { value: f, done: h } : {
          value: l ? [a(f[0]), a(f[1])] : a(f),
          done: h
        };
      },
      // iterable protocol
      [Symbol.iterator]() {
        return this;
      }
    };
  };
}
function Ce(t) {
  return function(...e) {
    return t === "delete" ? !1 : t === "clear" ? void 0 : this;
  };
}
function uc() {
  const t = {
    get(s) {
      return tr(this, s);
    },
    get size() {
      return nr(this);
    },
    has: er,
    add: Bs,
    set: zs,
    delete: Ds,
    clear: Vs,
    forEach: rr(!1, !1)
  }, e = {
    get(s) {
      return tr(this, s, !1, !0);
    },
    get size() {
      return nr(this);
    },
    has: er,
    add: Bs,
    set: zs,
    delete: Ds,
    clear: Vs,
    forEach: rr(!1, !0)
  }, n = {
    get(s) {
      return tr(this, s, !0);
    },
    get size() {
      return nr(this, !0);
    },
    has(s) {
      return er.call(this, s, !0);
    },
    add: Ce("add"),
    set: Ce("set"),
    delete: Ce("delete"),
    clear: Ce("clear"),
    forEach: rr(!0, !1)
  }, r = {
    get(s) {
      return tr(this, s, !0, !0);
    },
    get size() {
      return nr(this, !0);
    },
    has(s) {
      return er.call(this, s, !0);
    },
    add: Ce("add"),
    set: Ce("set"),
    delete: Ce("delete"),
    clear: Ce("clear"),
    forEach: rr(!0, !0)
  };
  return ["keys", "values", "entries", Symbol.iterator].forEach((s) => {
    t[s] = ir(
      s,
      !1,
      !1
    ), n[s] = ir(
      s,
      !0,
      !1
    ), e[s] = ir(
      s,
      !1,
      !0
    ), r[s] = ir(
      s,
      !0,
      !0
    );
  }), [
    t,
    n,
    e,
    r
  ];
}
const [
  cc,
  ac,
  fc,
  hc
] = /* @__PURE__ */ uc();
function os(t, e) {
  const n = e ? t ? hc : fc : t ? ac : cc;
  return (r, i, s) => i === "__v_isReactive" ? !t : i === "__v_isReadonly" ? t : i === "__v_raw" ? r : Reflect.get(
    ft(n, i) && i in r ? n : r,
    i,
    s
  );
}
const dc = {
  get: /* @__PURE__ */ os(!1, !1)
}, pc = {
  get: /* @__PURE__ */ os(!1, !0)
}, gc = {
  get: /* @__PURE__ */ os(!0, !1)
}, ul = /* @__PURE__ */ new WeakMap(), cl = /* @__PURE__ */ new WeakMap(), al = /* @__PURE__ */ new WeakMap(), mc = /* @__PURE__ */ new WeakMap();
function wc(t) {
  switch (t) {
    case "Object":
    case "Array":
      return 1;
    case "Map":
    case "Set":
    case "WeakMap":
    case "WeakSet":
      return 2;
    default:
      return 0;
  }
}
function yc(t) {
  return t.__v_skip || !Object.isExtensible(t) ? 0 : wc(Du(t));
}
function Wr(t) {
  return gn(t) ? t : ls(
    t,
    !1,
    sc,
    dc,
    ul
  );
}
function _c(t) {
  return ls(
    t,
    !1,
    lc,
    pc,
    cl
  );
}
function fl(t) {
  return ls(
    t,
    !0,
    oc,
    gc,
    al
  );
}
function ls(t, e, n, r, i) {
  if (!St(t) || t.__v_raw && !(e && t.__v_isReactive))
    return t;
  const s = i.get(t);
  if (s)
    return s;
  const o = yc(t);
  if (o === 0)
    return t;
  const l = new Proxy(
    t,
    o === 2 ? r : n
  );
  return i.set(t, l), l;
}
function an(t) {
  return gn(t) ? an(t.__v_raw) : !!(t && t.__v_isReactive);
}
function gn(t) {
  return !!(t && t.__v_isReadonly);
}
function Rr(t) {
  return !!(t && t.__v_isShallow);
}
function hl(t) {
  return an(t) || gn(t);
}
function dt(t) {
  const e = t && t.__v_raw;
  return e ? dt(e) : t;
}
function dl(t) {
  return Object.isExtensible(t) && Nr(t, "__v_skip", !0), t;
}
const An = (t) => St(t) ? Wr(t) : t, us = (t) => St(t) ? fl(t) : t;
class pl {
  constructor(e, n, r, i) {
    this.getter = e, this._setter = n, this.dep = void 0, this.__v_isRef = !0, this.__v_isReadonly = !1, this.effect = new ns(
      () => e(this._value),
      () => mr(
        this,
        this.effect._dirtyLevel === 2 ? 2 : 3
      )
    ), this.effect.computed = this, this.effect.active = this._cacheable = !i, this.__v_isReadonly = r;
  }
  get value() {
    const e = dt(this);
    return (!e._cacheable || e.effect.dirty) && ze(e._value, e._value = e.effect.run()) && mr(e, 4), gl(e), e.effect._dirtyLevel >= 2 && mr(e, 2), e._value;
  }
  set value(e) {
    this._setter(e);
  }
  // #region polyfill _dirty for backward compatibility third party code for Vue <= 3.3.x
  get _dirty() {
    return this.effect.dirty;
  }
  set _dirty(e) {
    this.effect.dirty = e;
  }
  // #endregion
}
function vc(t, e, n = !1) {
  let r, i;
  const s = ut(t);
  return s ? (r = t, i = ie) : (r = t.get, i = t.set), new pl(r, i, s || !i, n);
}
function gl(t) {
  var e;
  je && Ye && (t = dt(t), nl(
    Ye,
    (e = t.dep) != null ? e : t.dep = il(
      () => t.dep = void 0,
      t instanceof pl ? t : void 0
    )
  ));
}
function mr(t, e = 4, n) {
  t = dt(t);
  const r = t.dep;
  r && rl(
    r,
    e
  );
}
function Kt(t) {
  return !!(t && t.__v_isRef === !0);
}
function qs(t) {
  return bc(t, !1);
}
function bc(t, e) {
  return Kt(t) ? t : new xc(t, e);
}
class xc {
  constructor(e, n) {
    this.__v_isShallow = n, this.dep = void 0, this.__v_isRef = !0, this._rawValue = n ? e : dt(e), this._value = n ? e : An(e);
  }
  get value() {
    return gl(this), this._value;
  }
  set value(e) {
    const n = this.__v_isShallow || Rr(e) || gn(e);
    e = n ? e : dt(e), ze(e, this._rawValue) && (this._rawValue = e, this._value = n ? e : An(e), mr(this, 4));
  }
}
function Tr(t) {
  return Kt(t) ? t.value : t;
}
const Ec = {
  get: (t, e, n) => Tr(Reflect.get(t, e, n)),
  set: (t, e, n, r) => {
    const i = t[e];
    return Kt(i) && !Kt(n) ? (i.value = n, !0) : Reflect.set(t, e, n, r);
  }
};
function ml(t) {
  return an(t) ? t : new Proxy(t, Ec);
}
/**
* @vue/runtime-core v3.4.21
* (c) 2018-present Yuxi (Evan) You and Vue contributors
* @license MIT
**/
function Be(t, e, n, r) {
  try {
    return r ? t(...r) : t();
  } catch (i) {
    Kr(i, e, n);
  }
}
function fe(t, e, n, r) {
  if (ut(t)) {
    const s = Be(t, e, n, r);
    return s && Wo(s) && s.catch((o) => {
      Kr(o, e, n);
    }), s;
  }
  const i = [];
  for (let s = 0; s < t.length; s++)
    i.push(fe(t[s], e, n, r));
  return i;
}
function Kr(t, e, n, r = !0) {
  const i = e ? e.vnode : null;
  if (e) {
    let s = e.parent;
    const o = e.proxy, l = `https://vuejs.org/error-reference/#runtime-${n}`;
    for (; s; ) {
      const c = s.ec;
      if (c) {
        for (let a = 0; a < c.length; a++)
          if (c[a](t, o, l) === !1)
            return;
      }
      s = s.parent;
    }
    const u = e.appContext.config.errorHandler;
    if (u) {
      Be(
        u,
        null,
        10,
        [t, o, l]
      );
      return;
    }
  }
  Sc(t, n, i, r);
}
function Sc(t, e, n, r = !0) {
  console.error(t);
}
let Fn = !1, Ci = !1;
const Lt = [];
let ye = 0;
const fn = [];
let $e = null, We = 0;
const wl = /* @__PURE__ */ Promise.resolve();
let cs = null;
function yl(t) {
  const e = cs || wl;
  return t ? e.then(this ? t.bind(this) : t) : e;
}
function kc(t) {
  let e = ye + 1, n = Lt.length;
  for (; e < n; ) {
    const r = e + n >>> 1, i = Lt[r], s = jn(i);
    s < t || s === t && i.pre ? e = r + 1 : n = r;
  }
  return e;
}
function as(t) {
  (!Lt.length || !Lt.includes(
    t,
    Fn && t.allowRecurse ? ye + 1 : ye
  )) && (t.id == null ? Lt.push(t) : Lt.splice(kc(t.id), 0, t), _l());
}
function _l() {
  !Fn && !Ci && (Ci = !0, cs = wl.then(bl));
}
function Mc(t) {
  const e = Lt.indexOf(t);
  e > ye && Lt.splice(e, 1);
}
function Nc(t) {
  rt(t) ? fn.push(...t) : (!$e || !$e.includes(
    t,
    t.allowRecurse ? We + 1 : We
  )) && fn.push(t), _l();
}
function Gs(t, e, n = Fn ? ye + 1 : 0) {
  for (; n < Lt.length; n++) {
    const r = Lt[n];
    if (r && r.pre) {
      if (t && r.id !== t.uid)
        continue;
      Lt.splice(n, 1), n--, r();
    }
  }
}
function vl(t) {
  if (fn.length) {
    const e = [...new Set(fn)].sort(
      (n, r) => jn(n) - jn(r)
    );
    if (fn.length = 0, $e) {
      $e.push(...e);
      return;
    }
    for ($e = e, We = 0; We < $e.length; We++)
      $e[We]();
    $e = null, We = 0;
  }
}
const jn = (t) => t.id == null ? 1 / 0 : t.id, Rc = (t, e) => {
  const n = jn(t) - jn(e);
  if (n === 0) {
    if (t.pre && !e.pre)
      return -1;
    if (e.pre && !t.pre)
      return 1;
  }
  return n;
};
function bl(t) {
  Ci = !1, Fn = !0, Lt.sort(Rc);
  try {
    for (ye = 0; ye < Lt.length; ye++) {
      const e = Lt[ye];
      e && e.active !== !1 && Be(e, null, 14);
    }
  } finally {
    ye = 0, Lt.length = 0, vl(), Fn = !1, cs = null, (Lt.length || fn.length) && bl();
  }
}
function Tc(t, e, ...n) {
  if (t.isUnmounted)
    return;
  const r = t.vnode.props || bt;
  let i = n;
  const s = e.startsWith("update:"), o = s && e.slice(7);
  if (o && o in r) {
    const a = `${o === "modelValue" ? "model" : o}Modifiers`, { number: f, trim: h } = r[a] || bt;
    h && (i = n.map((p) => Ct(p) ? p.trim() : p)), f && (i = n.map(Gu));
  }
  let l, u = r[l = li(e)] || // also try camelCase event handler (#2249)
  r[l = li(ke(e))];
  !u && s && (u = r[l = li(ue(e))]), u && fe(
    u,
    t,
    6,
    i
  );
  const c = r[l + "Once"];
  if (c) {
    if (!t.emitted)
      t.emitted = {};
    else if (t.emitted[l])
      return;
    t.emitted[l] = !0, fe(
      c,
      t,
      6,
      i
    );
  }
}
function xl(t, e, n = !1) {
  const r = e.emitsCache, i = r.get(t);
  if (i !== void 0)
    return i;
  const s = t.emits;
  let o = {}, l = !1;
  if (!ut(t)) {
    const u = (c) => {
      const a = xl(c, e, !0);
      a && (l = !0, Tt(o, a));
    };
    !n && e.mixins.length && e.mixins.forEach(u), t.extends && u(t.extends), t.mixins && t.mixins.forEach(u);
  }
  return !s && !l ? (St(t) && r.set(t, null), null) : (rt(s) ? s.forEach((u) => o[u] = null) : Tt(o, s), St(t) && r.set(t, o), o);
}
function Xr(t, e) {
  return !t || !qr(e) ? !1 : (e = e.slice(2).replace(/Once$/, ""), ft(t, e[0].toLowerCase() + e.slice(1)) || ft(t, ue(e)) || ft(t, e));
}
let Jt = null, El = null;
function Cr(t) {
  const e = Jt;
  return Jt = t, El = t && t.type.__scopeId || null, e;
}
function Cc(t, e = Jt, n) {
  if (!e || t._n)
    return t;
  const r = (...i) => {
    r._d && eo(-1);
    const s = Cr(e);
    let o;
    try {
      o = t(...i);
    } finally {
      Cr(s), r._d && eo(1);
    }
    return o;
  };
  return r._n = !0, r._c = !0, r._d = !0, r;
}
function ai(t) {
  const {
    type: e,
    vnode: n,
    proxy: r,
    withProxy: i,
    props: s,
    propsOptions: [o],
    slots: l,
    attrs: u,
    emit: c,
    render: a,
    renderCache: f,
    data: h,
    setupState: p,
    ctx: w,
    inheritAttrs: _
  } = t;
  let v, d;
  const T = Cr(t);
  try {
    if (n.shapeFlag & 4) {
      const y = i || r, k = y;
      v = me(
        a.call(
          k,
          y,
          f,
          s,
          p,
          h,
          w
        )
      ), d = u;
    } else {
      const y = e;
      v = me(
        y.length > 1 ? y(
          s,
          { attrs: u, slots: l, emit: c }
        ) : y(
          s,
          null
          /* we know it doesn't need it */
        )
      ), d = e.props ? u : Pc(u);
    }
  } catch (y) {
    In.length = 0, Kr(y, t, 1), v = _e(Bn);
  }
  let P = v;
  if (d && _ !== !1) {
    const y = Object.keys(d), { shapeFlag: k } = P;
    y.length && k & 7 && (o && y.some(Ji) && (d = Lc(
      d,
      o
    )), P = mn(P, d));
  }
  return n.dirs && (P = mn(P), P.dirs = P.dirs ? P.dirs.concat(n.dirs) : n.dirs), n.transition && (P.transition = n.transition), v = P, Cr(T), v;
}
const Pc = (t) => {
  let e;
  for (const n in t)
    (n === "class" || n === "style" || qr(n)) && ((e || (e = {}))[n] = t[n]);
  return e;
}, Lc = (t, e) => {
  const n = {};
  for (const r in t)
    (!Ji(r) || !(r.slice(9) in e)) && (n[r] = t[r]);
  return n;
};
function Ic(t, e, n) {
  const { props: r, children: i, component: s } = t, { props: o, children: l, patchFlag: u } = e, c = s.emitsOptions;
  if (e.dirs || e.transition)
    return !0;
  if (n && u >= 0) {
    if (u & 1024)
      return !0;
    if (u & 16)
      return r ? Hs(r, o, c) : !!o;
    if (u & 8) {
      const a = e.dynamicProps;
      for (let f = 0; f < a.length; f++) {
        const h = a[f];
        if (o[h] !== r[h] && !Xr(c, h))
          return !0;
      }
    }
  } else
    return (i || l) && (!l || !l.$stable) ? !0 : r === o ? !1 : r ? o ? Hs(r, o, c) : !0 : !!o;
  return !1;
}
function Hs(t, e, n) {
  const r = Object.keys(e);
  if (r.length !== Object.keys(t).length)
    return !0;
  for (let i = 0; i < r.length; i++) {
    const s = r[i];
    if (e[s] !== t[s] && !Xr(n, s))
      return !0;
  }
  return !1;
}
function $c({ vnode: t, parent: e }, n) {
  for (; e; ) {
    const r = e.subTree;
    if (r.suspense && r.suspense.activeBranch === t && (r.el = t.el), r === t)
      (t = e.vnode).el = n, e = e.parent;
    else
      break;
  }
}
const Oc = Symbol.for("v-ndc"), Ac = (t) => t.__isSuspense;
function Fc(t, e) {
  e && e.pendingBranch ? rt(t) ? e.effects.push(...t) : e.effects.push(t) : Nc(t);
}
const jc = Symbol.for("v-scx"), Bc = () => _r(jc), sr = {};
function fi(t, e, n) {
  return Sl(t, e, n);
}
function Sl(t, e, {
  immediate: n,
  deep: r,
  flush: i,
  once: s,
  onTrack: o,
  onTrigger: l
} = bt) {
  if (e && s) {
    const L = e;
    e = (...z) => {
      L(...z), k();
    };
  }
  const u = Ft, c = (L) => r === !0 ? L : (
    // for deep: false, only traverse root-level properties
    Ke(L, r === !1 ? 1 : void 0)
  );
  let a, f = !1, h = !1;
  if (Kt(t) ? (a = () => t.value, f = Rr(t)) : an(t) ? (a = () => c(t), f = !0) : rt(t) ? (h = !0, f = t.some((L) => an(L) || Rr(L)), a = () => t.map((L) => {
    if (Kt(L))
      return L.value;
    if (an(L))
      return c(L);
    if (ut(L))
      return Be(L, u, 2);
  })) : ut(t) ? e ? a = () => Be(t, u, 2) : a = () => (p && p(), fe(
    t,
    u,
    3,
    [w]
  )) : a = ie, e && r) {
    const L = a;
    a = () => Ke(L());
  }
  let p, w = (L) => {
    p = P.onStop = () => {
      Be(L, u, 4), p = P.onStop = void 0;
    };
  }, _;
  if (Zr)
    if (w = ie, e ? n && fe(e, u, 3, [
      a(),
      h ? [] : void 0,
      w
    ]) : a(), i === "sync") {
      const L = Bc();
      _ = L.__watcherHandles || (L.__watcherHandles = []);
    } else
      return ie;
  let v = h ? new Array(t.length).fill(sr) : sr;
  const d = () => {
    if (!(!P.active || !P.dirty))
      if (e) {
        const L = P.run();
        (r || f || (h ? L.some((z, G) => ze(z, v[G])) : ze(L, v))) && (p && p(), fe(e, u, 3, [
          L,
          // pass undefined as the old value when it's changed for the first time
          v === sr ? void 0 : h && v[0] === sr ? [] : v,
          w
        ]), v = L);
      } else
        P.run();
  };
  d.allowRecurse = !!e;
  let T;
  i === "sync" ? T = d : i === "post" ? T = () => Gt(d, u && u.suspense) : (d.pre = !0, u && (d.id = u.uid), T = () => as(d));
  const P = new ns(a, ie, T), y = Zu(), k = () => {
    P.stop(), y && Qi(y.effects, P);
  };
  return e ? n ? d() : v = P.run() : i === "post" ? Gt(
    P.run.bind(P),
    u && u.suspense
  ) : P.run(), _ && _.push(k), k;
}
function zc(t, e, n) {
  const r = this.proxy, i = Ct(t) ? t.includes(".") ? kl(r, t) : () => r[t] : t.bind(r, r);
  let s;
  ut(e) ? s = e : (s = e.handler, n = e);
  const o = Wn(this), l = Sl(i, s.bind(r), n);
  return o(), l;
}
function kl(t, e) {
  const n = e.split(".");
  return () => {
    let r = t;
    for (let i = 0; i < n.length && r; i++)
      r = r[n[i]];
    return r;
  };
}
function Ke(t, e, n = 0, r) {
  if (!St(t) || t.__v_skip)
    return t;
  if (e && e > 0) {
    if (n >= e)
      return t;
    n++;
  }
  if (r = r || /* @__PURE__ */ new Set(), r.has(t))
    return t;
  if (r.add(t), Kt(t))
    Ke(t.value, e, n, r);
  else if (rt(t))
    for (let i = 0; i < t.length; i++)
      Ke(t[i], e, n, r);
  else if (Uo(t) || cn(t))
    t.forEach((i) => {
      Ke(i, e, n, r);
    });
  else if (Xo(t))
    for (const i in t)
      Ke(t[i], e, n, r);
  return t;
}
function wr(t, e) {
  if (Jt === null)
    return t;
  const n = ti(Jt) || Jt.proxy, r = t.dirs || (t.dirs = []);
  for (let i = 0; i < e.length; i++) {
    let [s, o, l, u = bt] = e[i];
    s && (ut(s) && (s = {
      mounted: s,
      updated: s
    }), s.deep && Ke(o), r.push({
      dir: s,
      instance: n,
      value: o,
      oldValue: void 0,
      arg: l,
      modifiers: u
    }));
  }
  return t;
}
function qe(t, e, n, r) {
  const i = t.dirs, s = e && e.dirs;
  for (let o = 0; o < i.length; o++) {
    const l = i[o];
    s && (l.oldValue = s[o].value);
    let u = l.dir[r];
    u && (en(), fe(u, n, 8, [
      t.el,
      l,
      t,
      e
    ]), nn());
  }
}
/*! #__NO_SIDE_EFFECTS__ */
// @__NO_SIDE_EFFECTS__
function fs(t, e) {
  return ut(t) ? (
    // #8326: extend call and options.name access are considered side-effects
    // by Rollup, so we have to wrap it in a pure-annotated IIFE.
    Tt({ name: t.name }, e, { setup: t })
  ) : t;
}
const yr = (t) => !!t.type.__asyncLoader, Ml = (t) => t.type.__isKeepAlive;
function Dc(t, e) {
  Nl(t, "a", e);
}
function Vc(t, e) {
  Nl(t, "da", e);
}
function Nl(t, e, n = Ft) {
  const r = t.__wdc || (t.__wdc = () => {
    let i = n;
    for (; i; ) {
      if (i.isDeactivated)
        return;
      i = i.parent;
    }
    return t();
  });
  if (Yr(e, r, n), n) {
    let i = n.parent;
    for (; i && i.parent; )
      Ml(i.parent.vnode) && qc(r, e, n, i), i = i.parent;
  }
}
function qc(t, e, n, r) {
  const i = Yr(
    e,
    t,
    r,
    !0
    /* prepend */
  );
  hs(() => {
    Qi(r[e], i);
  }, n);
}
function Yr(t, e, n = Ft, r = !1) {
  if (n) {
    const i = n[t] || (n[t] = []), s = e.__weh || (e.__weh = (...o) => {
      if (n.isUnmounted)
        return;
      en();
      const l = Wn(n), u = fe(e, n, t, o);
      return l(), nn(), u;
    });
    return r ? i.unshift(s) : i.push(s), s;
  }
}
const Re = (t) => (e, n = Ft) => (
  // post-create lifecycle registrations are noops during SSR (except for serverPrefetch)
  (!Zr || t === "sp") && Yr(t, (...r) => e(...r), n)
), Rl = Re("bm"), Tl = Re("m"), Gc = Re("bu"), Hc = Re("u"), Uc = Re("bum"), hs = Re("um"), Wc = Re("sp"), Kc = Re(
  "rtg"
), Xc = Re(
  "rtc"
);
function Yc(t, e = Ft) {
  Yr("ec", t, e);
}
function Us(t, e, n, r) {
  let i;
  const s = n;
  if (rt(t) || Ct(t)) {
    i = new Array(t.length);
    for (let o = 0, l = t.length; o < l; o++)
      i[o] = e(t[o], o, void 0, s);
  } else if (typeof t == "number") {
    i = new Array(t);
    for (let o = 0; o < t; o++)
      i[o] = e(o + 1, o, void 0, s);
  } else if (St(t))
    if (t[Symbol.iterator])
      i = Array.from(
        t,
        (o, l) => e(o, l, void 0, s)
      );
    else {
      const o = Object.keys(t);
      i = new Array(o.length);
      for (let l = 0, u = o.length; l < u; l++) {
        const c = o[l];
        i[l] = e(t[c], c, l, s);
      }
    }
  else
    i = [];
  return i;
}
const Pi = (t) => t ? zl(t) ? ti(t) || t.proxy : Pi(t.parent) : null, Pn = (
  // Move PURE marker to new line to workaround compiler discarding it
  // due to type annotation
  /* @__PURE__ */ Tt(/* @__PURE__ */ Object.create(null), {
    $: (t) => t,
    $el: (t) => t.vnode.el,
    $data: (t) => t.data,
    $props: (t) => t.props,
    $attrs: (t) => t.attrs,
    $slots: (t) => t.slots,
    $refs: (t) => t.refs,
    $parent: (t) => Pi(t.parent),
    $root: (t) => Pi(t.root),
    $emit: (t) => t.emit,
    $options: (t) => ds(t),
    $forceUpdate: (t) => t.f || (t.f = () => {
      t.effect.dirty = !0, as(t.update);
    }),
    $nextTick: (t) => t.n || (t.n = yl.bind(t.proxy)),
    $watch: (t) => zc.bind(t)
  })
), hi = (t, e) => t !== bt && !t.__isScriptSetup && ft(t, e), Jc = {
  get({ _: t }, e) {
    const { ctx: n, setupState: r, data: i, props: s, accessCache: o, type: l, appContext: u } = t;
    let c;
    if (e[0] !== "$") {
      const p = o[e];
      if (p !== void 0)
        switch (p) {
          case 1:
            return r[e];
          case 2:
            return i[e];
          case 4:
            return n[e];
          case 3:
            return s[e];
        }
      else {
        if (hi(r, e))
          return o[e] = 1, r[e];
        if (i !== bt && ft(i, e))
          return o[e] = 2, i[e];
        if (
          // only cache other properties when instance has declared (thus stable)
          // props
          (c = t.propsOptions[0]) && ft(c, e)
        )
          return o[e] = 3, s[e];
        if (n !== bt && ft(n, e))
          return o[e] = 4, n[e];
        Li && (o[e] = 0);
      }
    }
    const a = Pn[e];
    let f, h;
    if (a)
      return e === "$attrs" && Wt(t, "get", e), a(t);
    if (
      // css module (injected by vue-loader)
      (f = l.__cssModules) && (f = f[e])
    )
      return f;
    if (n !== bt && ft(n, e))
      return o[e] = 4, n[e];
    if (
      // global properties
      h = u.config.globalProperties, ft(h, e)
    )
      return h[e];
  },
  set({ _: t }, e, n) {
    const { data: r, setupState: i, ctx: s } = t;
    return hi(i, e) ? (i[e] = n, !0) : r !== bt && ft(r, e) ? (r[e] = n, !0) : ft(t.props, e) || e[0] === "$" && e.slice(1) in t ? !1 : (s[e] = n, !0);
  },
  has({
    _: { data: t, setupState: e, accessCache: n, ctx: r, appContext: i, propsOptions: s }
  }, o) {
    let l;
    return !!n[o] || t !== bt && ft(t, o) || hi(e, o) || (l = s[0]) && ft(l, o) || ft(r, o) || ft(Pn, o) || ft(i.config.globalProperties, o);
  },
  defineProperty(t, e, n) {
    return n.get != null ? t._.accessCache[e] = 0 : ft(n, "value") && this.set(t, e, n.value, null), Reflect.defineProperty(t, e, n);
  }
};
function Ws(t) {
  return rt(t) ? t.reduce(
    (e, n) => (e[n] = null, e),
    {}
  ) : t;
}
let Li = !0;
function Qc(t) {
  const e = ds(t), n = t.proxy, r = t.ctx;
  Li = !1, e.beforeCreate && Ks(e.beforeCreate, t, "bc");
  const {
    // state
    data: i,
    computed: s,
    methods: o,
    watch: l,
    provide: u,
    inject: c,
    // lifecycle
    created: a,
    beforeMount: f,
    mounted: h,
    beforeUpdate: p,
    updated: w,
    activated: _,
    deactivated: v,
    beforeDestroy: d,
    beforeUnmount: T,
    destroyed: P,
    unmounted: y,
    render: k,
    renderTracked: L,
    renderTriggered: z,
    errorCaptured: G,
    serverPrefetch: Q,
    // public API
    expose: X,
    inheritAttrs: et,
    // assets
    components: ot,
    directives: U,
    filters: x
  } = e;
  if (c && Zc(c, r, null), o)
    for (const j in o) {
      const B = o[j];
      ut(B) && (r[j] = B.bind(n));
    }
  if (i) {
    const j = i.call(n, n);
    St(j) && (t.data = Wr(j));
  }
  if (Li = !0, s)
    for (const j in s) {
      const B = s[j], Y = ut(B) ? B.bind(n, n) : ut(B.get) ? B.get.bind(n, n) : ie, J = !ut(B) && ut(B.set) ? B.set.bind(n) : ie, it = Fi({
        get: Y,
        set: J
      });
      Object.defineProperty(r, j, {
        enumerable: !0,
        configurable: !0,
        get: () => it.value,
        set: (lt) => it.value = lt
      });
    }
  if (l)
    for (const j in l)
      Cl(l[j], r, n, j);
  if (u) {
    const j = ut(u) ? u.call(n) : u;
    Reflect.ownKeys(j).forEach((B) => {
      sa(B, j[B]);
    });
  }
  a && Ks(a, t, "c");
  function N(j, B) {
    rt(B) ? B.forEach((Y) => j(Y.bind(n))) : B && j(B.bind(n));
  }
  if (N(Rl, f), N(Tl, h), N(Gc, p), N(Hc, w), N(Dc, _), N(Vc, v), N(Yc, G), N(Xc, L), N(Kc, z), N(Uc, T), N(hs, y), N(Wc, Q), rt(X))
    if (X.length) {
      const j = t.exposed || (t.exposed = {});
      X.forEach((B) => {
        Object.defineProperty(j, B, {
          get: () => n[B],
          set: (Y) => n[B] = Y
        });
      });
    } else t.exposed || (t.exposed = {});
  k && t.render === ie && (t.render = k), et != null && (t.inheritAttrs = et), ot && (t.components = ot), U && (t.directives = U);
}
function Zc(t, e, n = ie) {
  rt(t) && (t = Ii(t));
  for (const r in t) {
    const i = t[r];
    let s;
    St(i) ? "default" in i ? s = _r(
      i.from || r,
      i.default,
      !0
    ) : s = _r(i.from || r) : s = _r(i), Kt(s) ? Object.defineProperty(e, r, {
      enumerable: !0,
      configurable: !0,
      get: () => s.value,
      set: (o) => s.value = o
    }) : e[r] = s;
  }
}
function Ks(t, e, n) {
  fe(
    rt(t) ? t.map((r) => r.bind(e.proxy)) : t.bind(e.proxy),
    e,
    n
  );
}
function Cl(t, e, n, r) {
  const i = r.includes(".") ? kl(n, r) : () => n[r];
  if (Ct(t)) {
    const s = e[t];
    ut(s) && fi(i, s);
  } else if (ut(t))
    fi(i, t.bind(n));
  else if (St(t))
    if (rt(t))
      t.forEach((s) => Cl(s, e, n, r));
    else {
      const s = ut(t.handler) ? t.handler.bind(n) : e[t.handler];
      ut(s) && fi(i, s, t);
    }
}
function ds(t) {
  const e = t.type, { mixins: n, extends: r } = e, {
    mixins: i,
    optionsCache: s,
    config: { optionMergeStrategies: o }
  } = t.appContext, l = s.get(e);
  let u;
  return l ? u = l : !i.length && !n && !r ? u = e : (u = {}, i.length && i.forEach(
    (c) => Pr(u, c, o, !0)
  ), Pr(u, e, o)), St(e) && s.set(e, u), u;
}
function Pr(t, e, n, r = !1) {
  const { mixins: i, extends: s } = e;
  s && Pr(t, s, n, !0), i && i.forEach(
    (o) => Pr(t, o, n, !0)
  );
  for (const o in e)
    if (!(r && o === "expose")) {
      const l = ta[o] || n && n[o];
      t[o] = l ? l(t[o], e[o]) : e[o];
    }
  return t;
}
const ta = {
  data: Xs,
  props: Ys,
  emits: Ys,
  // objects
  methods: Nn,
  computed: Nn,
  // lifecycle
  beforeCreate: Ot,
  created: Ot,
  beforeMount: Ot,
  mounted: Ot,
  beforeUpdate: Ot,
  updated: Ot,
  beforeDestroy: Ot,
  beforeUnmount: Ot,
  destroyed: Ot,
  unmounted: Ot,
  activated: Ot,
  deactivated: Ot,
  errorCaptured: Ot,
  serverPrefetch: Ot,
  // assets
  components: Nn,
  directives: Nn,
  // watch
  watch: na,
  // provide / inject
  provide: Xs,
  inject: ea
};
function Xs(t, e) {
  return e ? t ? function() {
    return Tt(
      ut(t) ? t.call(this, this) : t,
      ut(e) ? e.call(this, this) : e
    );
  } : e : t;
}
function ea(t, e) {
  return Nn(Ii(t), Ii(e));
}
function Ii(t) {
  if (rt(t)) {
    const e = {};
    for (let n = 0; n < t.length; n++)
      e[t[n]] = t[n];
    return e;
  }
  return t;
}
function Ot(t, e) {
  return t ? [...new Set([].concat(t, e))] : e;
}
function Nn(t, e) {
  return t ? Tt(/* @__PURE__ */ Object.create(null), t, e) : e;
}
function Ys(t, e) {
  return t ? rt(t) && rt(e) ? [.../* @__PURE__ */ new Set([...t, ...e])] : Tt(
    /* @__PURE__ */ Object.create(null),
    Ws(t),
    Ws(e ?? {})
  ) : e;
}
function na(t, e) {
  if (!t)
    return e;
  if (!e)
    return t;
  const n = Tt(/* @__PURE__ */ Object.create(null), t);
  for (const r in e)
    n[r] = Ot(t[r], e[r]);
  return n;
}
function Pl() {
  return {
    app: null,
    config: {
      isNativeTag: Bu,
      performance: !1,
      globalProperties: {},
      optionMergeStrategies: {},
      errorHandler: void 0,
      warnHandler: void 0,
      compilerOptions: {}
    },
    mixins: [],
    components: {},
    directives: {},
    provides: /* @__PURE__ */ Object.create(null),
    optionsCache: /* @__PURE__ */ new WeakMap(),
    propsCache: /* @__PURE__ */ new WeakMap(),
    emitsCache: /* @__PURE__ */ new WeakMap()
  };
}
let ra = 0;
function ia(t, e) {
  return function(r, i = null) {
    ut(r) || (r = Tt({}, r)), i != null && !St(i) && (i = null);
    const s = Pl(), o = /* @__PURE__ */ new WeakSet();
    let l = !1;
    const u = s.app = {
      _uid: ra++,
      _component: r,
      _props: i,
      _container: null,
      _context: s,
      _instance: null,
      version: Pa,
      get config() {
        return s.config;
      },
      set config(c) {
      },
      use(c, ...a) {
        return o.has(c) || (c && ut(c.install) ? (o.add(c), c.install(u, ...a)) : ut(c) && (o.add(c), c(u, ...a))), u;
      },
      mixin(c) {
        return s.mixins.includes(c) || s.mixins.push(c), u;
      },
      component(c, a) {
        return a ? (s.components[c] = a, u) : s.components[c];
      },
      directive(c, a) {
        return a ? (s.directives[c] = a, u) : s.directives[c];
      },
      mount(c, a, f) {
        if (!l) {
          const h = _e(r, i);
          return h.appContext = s, f === !0 ? f = "svg" : f === !1 && (f = void 0), a && e ? e(h, c) : t(h, c, f), l = !0, u._container = c, c.__vue_app__ = u, ti(h.component) || h.component.proxy;
        }
      },
      unmount() {
        l && (t(null, u._container), delete u._container.__vue_app__);
      },
      provide(c, a) {
        return s.provides[c] = a, u;
      },
      runWithContext(c) {
        const a = Ln;
        Ln = u;
        try {
          return c();
        } finally {
          Ln = a;
        }
      }
    };
    return u;
  };
}
let Ln = null;
function sa(t, e) {
  if (Ft) {
    let n = Ft.provides;
    const r = Ft.parent && Ft.parent.provides;
    r === n && (n = Ft.provides = Object.create(r)), n[t] = e;
  }
}
function _r(t, e, n = !1) {
  const r = Ft || Jt;
  if (r || Ln) {
    const i = r ? r.parent == null ? r.vnode.appContext && r.vnode.appContext.provides : r.parent.provides : Ln._context.provides;
    if (i && t in i)
      return i[t];
    if (arguments.length > 1)
      return n && ut(e) ? e.call(r && r.proxy) : e;
  }
}
function oa(t, e, n, r = !1) {
  const i = {}, s = {};
  Nr(s, Qr, 1), t.propsDefaults = /* @__PURE__ */ Object.create(null), Ll(t, e, i, s);
  for (const o in t.propsOptions[0])
    o in i || (i[o] = void 0);
  n ? t.props = r ? i : _c(i) : t.type.props ? t.props = i : t.props = s, t.attrs = s;
}
function la(t, e, n, r) {
  const {
    props: i,
    attrs: s,
    vnode: { patchFlag: o }
  } = t, l = dt(i), [u] = t.propsOptions;
  let c = !1;
  if (
    // always force full diff in dev
    // - #1942 if hmr is enabled with sfc component
    // - vite#872 non-sfc component used by sfc component
    (r || o > 0) && !(o & 16)
  ) {
    if (o & 8) {
      const a = t.vnode.dynamicProps;
      for (let f = 0; f < a.length; f++) {
        let h = a[f];
        if (Xr(t.emitsOptions, h))
          continue;
        const p = e[h];
        if (u)
          if (ft(s, h))
            p !== s[h] && (s[h] = p, c = !0);
          else {
            const w = ke(h);
            i[w] = $i(
              u,
              l,
              w,
              p,
              t,
              !1
            );
          }
        else
          p !== s[h] && (s[h] = p, c = !0);
      }
    }
  } else {
    Ll(t, e, i, s) && (c = !0);
    let a;
    for (const f in l)
      (!e || // for camelCase
      !ft(e, f) && // it's possible the original props was passed in as kebab-case
      // and converted to camelCase (#955)
      ((a = ue(f)) === f || !ft(e, a))) && (u ? n && // for camelCase
      (n[f] !== void 0 || // for kebab-case
      n[a] !== void 0) && (i[f] = $i(
        u,
        l,
        f,
        void 0,
        t,
        !0
      )) : delete i[f]);
    if (s !== l)
      for (const f in s)
        (!e || !ft(e, f)) && (delete s[f], c = !0);
  }
  c && Me(t, "set", "$attrs");
}
function Ll(t, e, n, r) {
  const [i, s] = t.propsOptions;
  let o = !1, l;
  if (e)
    for (let u in e) {
      if (Cn(u))
        continue;
      const c = e[u];
      let a;
      i && ft(i, a = ke(u)) ? !s || !s.includes(a) ? n[a] = c : (l || (l = {}))[a] = c : Xr(t.emitsOptions, u) || (!(u in r) || c !== r[u]) && (r[u] = c, o = !0);
    }
  if (s) {
    const u = dt(n), c = l || bt;
    for (let a = 0; a < s.length; a++) {
      const f = s[a];
      n[f] = $i(
        i,
        u,
        f,
        c[f],
        t,
        !ft(c, f)
      );
    }
  }
  return o;
}
function $i(t, e, n, r, i, s) {
  const o = t[n];
  if (o != null) {
    const l = ft(o, "default");
    if (l && r === void 0) {
      const u = o.default;
      if (o.type !== Function && !o.skipFactory && ut(u)) {
        const { propsDefaults: c } = i;
        if (n in c)
          r = c[n];
        else {
          const a = Wn(i);
          r = c[n] = u.call(
            null,
            e
          ), a();
        }
      } else
        r = u;
    }
    o[
      0
      /* shouldCast */
    ] && (s && !l ? r = !1 : o[
      1
      /* shouldCastTrue */
    ] && (r === "" || r === ue(n)) && (r = !0));
  }
  return r;
}
function Il(t, e, n = !1) {
  const r = e.propsCache, i = r.get(t);
  if (i)
    return i;
  const s = t.props, o = {}, l = [];
  let u = !1;
  if (!ut(t)) {
    const a = (f) => {
      u = !0;
      const [h, p] = Il(f, e, !0);
      Tt(o, h), p && l.push(...p);
    };
    !n && e.mixins.length && e.mixins.forEach(a), t.extends && a(t.extends), t.mixins && t.mixins.forEach(a);
  }
  if (!s && !u)
    return St(t) && r.set(t, un), un;
  if (rt(s))
    for (let a = 0; a < s.length; a++) {
      const f = ke(s[a]);
      Js(f) && (o[f] = bt);
    }
  else if (s)
    for (const a in s) {
      const f = ke(a);
      if (Js(f)) {
        const h = s[a], p = o[f] = rt(h) || ut(h) ? { type: h } : Tt({}, h);
        if (p) {
          const w = to(Boolean, p.type), _ = to(String, p.type);
          p[
            0
            /* shouldCast */
          ] = w > -1, p[
            1
            /* shouldCastTrue */
          ] = _ < 0 || w < _, (w > -1 || ft(p, "default")) && l.push(f);
        }
      }
    }
  const c = [o, l];
  return St(t) && r.set(t, c), c;
}
function Js(t) {
  return t[0] !== "$" && !Cn(t);
}
function Qs(t) {
  return t === null ? "null" : typeof t == "function" ? t.name || "" : typeof t == "object" && t.constructor && t.constructor.name || "";
}
function Zs(t, e) {
  return Qs(t) === Qs(e);
}
function to(t, e) {
  return rt(e) ? e.findIndex((n) => Zs(n, t)) : ut(e) && Zs(e, t) ? 0 : -1;
}
const $l = (t) => t[0] === "_" || t === "$stable", ps = (t) => rt(t) ? t.map(me) : [me(t)], ua = (t, e, n) => {
  if (e._n)
    return e;
  const r = Cc((...i) => ps(e(...i)), n);
  return r._c = !1, r;
}, Ol = (t, e, n) => {
  const r = t._ctx;
  for (const i in t) {
    if ($l(i))
      continue;
    const s = t[i];
    if (ut(s))
      e[i] = ua(i, s, r);
    else if (s != null) {
      const o = ps(s);
      e[i] = () => o;
    }
  }
}, Al = (t, e) => {
  const n = ps(e);
  t.slots.default = () => n;
}, ca = (t, e) => {
  if (t.vnode.shapeFlag & 32) {
    const n = e._;
    n ? (t.slots = dt(e), Nr(e, "_", n)) : Ol(
      e,
      t.slots = {}
    );
  } else
    t.slots = {}, e && Al(t, e);
  Nr(t.slots, Qr, 1);
}, aa = (t, e, n) => {
  const { vnode: r, slots: i } = t;
  let s = !0, o = bt;
  if (r.shapeFlag & 32) {
    const l = e._;
    l ? n && l === 1 ? s = !1 : (Tt(i, e), !n && l === 1 && delete i._) : (s = !e.$stable, Ol(e, i)), o = e;
  } else e && (Al(t, e), o = { default: 1 });
  if (s)
    for (const l in i)
      !$l(l) && o[l] == null && delete i[l];
};
function Oi(t, e, n, r, i = !1) {
  if (rt(t)) {
    t.forEach(
      (h, p) => Oi(
        h,
        e && (rt(e) ? e[p] : e),
        n,
        r,
        i
      )
    );
    return;
  }
  if (yr(r) && !i)
    return;
  const s = r.shapeFlag & 4 ? ti(r.component) || r.component.proxy : r.el, o = i ? null : s, { i: l, r: u } = t, c = e && e.r, a = l.refs === bt ? l.refs = {} : l.refs, f = l.setupState;
  if (c != null && c !== u && (Ct(c) ? (a[c] = null, ft(f, c) && (f[c] = null)) : Kt(c) && (c.value = null)), ut(u))
    Be(u, l, 12, [o, a]);
  else {
    const h = Ct(u), p = Kt(u);
    if (h || p) {
      const w = () => {
        if (t.f) {
          const _ = h ? ft(f, u) ? f[u] : a[u] : u.value;
          i ? rt(_) && Qi(_, s) : rt(_) ? _.includes(s) || _.push(s) : h ? (a[u] = [s], ft(f, u) && (f[u] = a[u])) : (u.value = [s], t.k && (a[t.k] = u.value));
        } else h ? (a[u] = o, ft(f, u) && (f[u] = o)) : p && (u.value = o, t.k && (a[t.k] = o));
      };
      o ? (w.id = -1, Gt(w, n)) : w();
    }
  }
}
const Gt = Fc;
function fa(t) {
  return ha(t);
}
function ha(t, e) {
  const n = Jo();
  n.__VUE__ = !0;
  const {
    insert: r,
    remove: i,
    patchProp: s,
    createElement: o,
    createText: l,
    createComment: u,
    setText: c,
    setElementText: a,
    parentNode: f,
    nextSibling: h,
    setScopeId: p = ie,
    insertStaticContent: w
  } = t, _ = (m, b, C, $ = null, A = null, q = null, W = void 0, V = null, H = !!b.dynamicChildren) => {
    if (m === b)
      return;
    m && !xn(m, b) && ($ = xt(m), lt(m, A, q, !0), m = null), b.patchFlag === -2 && (H = !1, b.dynamicChildren = null);
    const { type: F, ref: K, shapeFlag: tt } = b;
    switch (F) {
      case Jr:
        v(m, b, C, $);
        break;
      case Bn:
        d(m, b, C, $);
        break;
      case pi:
        m == null && T(b, C, $, W);
        break;
      case re:
        ot(
          m,
          b,
          C,
          $,
          A,
          q,
          W,
          V,
          H
        );
        break;
      default:
        tt & 1 ? k(
          m,
          b,
          C,
          $,
          A,
          q,
          W,
          V,
          H
        ) : tt & 6 ? U(
          m,
          b,
          C,
          $,
          A,
          q,
          W,
          V,
          H
        ) : (tt & 64 || tt & 128) && F.process(
          m,
          b,
          C,
          $,
          A,
          q,
          W,
          V,
          H,
          Zt
        );
    }
    K != null && A && Oi(K, m && m.ref, q, b || m, !b);
  }, v = (m, b, C, $) => {
    if (m == null)
      r(
        b.el = l(b.children),
        C,
        $
      );
    else {
      const A = b.el = m.el;
      b.children !== m.children && c(A, b.children);
    }
  }, d = (m, b, C, $) => {
    m == null ? r(
      b.el = u(b.children || ""),
      C,
      $
    ) : b.el = m.el;
  }, T = (m, b, C, $) => {
    [m.el, m.anchor] = w(
      m.children,
      b,
      C,
      $,
      m.el,
      m.anchor
    );
  }, P = ({ el: m, anchor: b }, C, $) => {
    let A;
    for (; m && m !== b; )
      A = h(m), r(m, C, $), m = A;
    r(b, C, $);
  }, y = ({ el: m, anchor: b }) => {
    let C;
    for (; m && m !== b; )
      C = h(m), i(m), m = C;
    i(b);
  }, k = (m, b, C, $, A, q, W, V, H) => {
    b.type === "svg" ? W = "svg" : b.type === "math" && (W = "mathml"), m == null ? L(
      b,
      C,
      $,
      A,
      q,
      W,
      V,
      H
    ) : Q(
      m,
      b,
      A,
      q,
      W,
      V,
      H
    );
  }, L = (m, b, C, $, A, q, W, V) => {
    let H, F;
    const { props: K, shapeFlag: tt, transition: Z, dirs: nt } = m;
    if (H = m.el = o(
      m.type,
      q,
      K && K.is,
      K
    ), tt & 8 ? a(H, m.children) : tt & 16 && G(
      m.children,
      H,
      null,
      $,
      A,
      di(m, q),
      W,
      V
    ), nt && qe(m, null, $, "created"), z(H, m, m.scopeId, W, $), K) {
      for (const ht in K)
        ht !== "value" && !Cn(ht) && s(
          H,
          ht,
          null,
          K[ht],
          q,
          m.children,
          $,
          A,
          mt
        );
      "value" in K && s(H, "value", null, K.value, q), (F = K.onVnodeBeforeMount) && pe(F, $, m);
    }
    nt && qe(m, null, $, "beforeMount");
    const ct = da(A, Z);
    ct && Z.beforeEnter(H), r(H, b, C), ((F = K && K.onVnodeMounted) || ct || nt) && Gt(() => {
      F && pe(F, $, m), ct && Z.enter(H), nt && qe(m, null, $, "mounted");
    }, A);
  }, z = (m, b, C, $, A) => {
    if (C && p(m, C), $)
      for (let q = 0; q < $.length; q++)
        p(m, $[q]);
    if (A) {
      let q = A.subTree;
      if (b === q) {
        const W = A.vnode;
        z(
          m,
          W,
          W.scopeId,
          W.slotScopeIds,
          A.parent
        );
      }
    }
  }, G = (m, b, C, $, A, q, W, V, H = 0) => {
    for (let F = H; F < m.length; F++) {
      const K = m[F] = V ? Oe(m[F]) : me(m[F]);
      _(
        null,
        K,
        b,
        C,
        $,
        A,
        q,
        W,
        V
      );
    }
  }, Q = (m, b, C, $, A, q, W) => {
    const V = b.el = m.el;
    let { patchFlag: H, dynamicChildren: F, dirs: K } = b;
    H |= m.patchFlag & 16;
    const tt = m.props || bt, Z = b.props || bt;
    let nt;
    if (C && Ge(C, !1), (nt = Z.onVnodeBeforeUpdate) && pe(nt, C, b, m), K && qe(b, m, C, "beforeUpdate"), C && Ge(C, !0), F ? X(
      m.dynamicChildren,
      F,
      V,
      C,
      $,
      di(b, A),
      q
    ) : W || B(
      m,
      b,
      V,
      null,
      C,
      $,
      di(b, A),
      q,
      !1
    ), H > 0) {
      if (H & 16)
        et(
          V,
          b,
          tt,
          Z,
          C,
          $,
          A
        );
      else if (H & 2 && tt.class !== Z.class && s(V, "class", null, Z.class, A), H & 4 && s(V, "style", tt.style, Z.style, A), H & 8) {
        const ct = b.dynamicProps;
        for (let ht = 0; ht < ct.length; ht++) {
          const vt = ct[ht], Rt = tt[vt], Vt = Z[vt];
          (Vt !== Rt || vt === "value") && s(
            V,
            vt,
            Rt,
            Vt,
            A,
            m.children,
            C,
            $,
            mt
          );
        }
      }
      H & 1 && m.children !== b.children && a(V, b.children);
    } else !W && F == null && et(
      V,
      b,
      tt,
      Z,
      C,
      $,
      A
    );
    ((nt = Z.onVnodeUpdated) || K) && Gt(() => {
      nt && pe(nt, C, b, m), K && qe(b, m, C, "updated");
    }, $);
  }, X = (m, b, C, $, A, q, W) => {
    for (let V = 0; V < b.length; V++) {
      const H = m[V], F = b[V], K = (
        // oldVNode may be an errored async setup() component inside Suspense
        // which will not have a mounted element
        H.el && // - In the case of a Fragment, we need to provide the actual parent
        // of the Fragment itself so it can move its children.
        (H.type === re || // - In the case of different nodes, there is going to be a replacement
        // which also requires the correct parent container
        !xn(H, F) || // - In the case of a component, it could contain anything.
        H.shapeFlag & 70) ? f(H.el) : (
          // In other cases, the parent container is not actually used so we
          // just pass the block element here to avoid a DOM parentNode call.
          C
        )
      );
      _(
        H,
        F,
        K,
        null,
        $,
        A,
        q,
        W,
        !0
      );
    }
  }, et = (m, b, C, $, A, q, W) => {
    if (C !== $) {
      if (C !== bt)
        for (const V in C)
          !Cn(V) && !(V in $) && s(
            m,
            V,
            C[V],
            null,
            W,
            b.children,
            A,
            q,
            mt
          );
      for (const V in $) {
        if (Cn(V))
          continue;
        const H = $[V], F = C[V];
        H !== F && V !== "value" && s(
          m,
          V,
          F,
          H,
          W,
          b.children,
          A,
          q,
          mt
        );
      }
      "value" in $ && s(m, "value", C.value, $.value, W);
    }
  }, ot = (m, b, C, $, A, q, W, V, H) => {
    const F = b.el = m ? m.el : l(""), K = b.anchor = m ? m.anchor : l("");
    let { patchFlag: tt, dynamicChildren: Z, slotScopeIds: nt } = b;
    nt && (V = V ? V.concat(nt) : nt), m == null ? (r(F, C, $), r(K, C, $), G(
      // #10007
      // such fragment like `<></>` will be compiled into
      // a fragment which doesn't have a children.
      // In this case fallback to an empty array
      b.children || [],
      C,
      K,
      A,
      q,
      W,
      V,
      H
    )) : tt > 0 && tt & 64 && Z && // #2715 the previous fragment could've been a BAILed one as a result
    // of renderSlot() with no valid children
    m.dynamicChildren ? (X(
      m.dynamicChildren,
      Z,
      C,
      A,
      q,
      W,
      V
    ), // #2080 if the stable fragment has a key, it's a <template v-for> that may
    //  get moved around. Make sure all root level vnodes inherit el.
    // #2134 or if it's a component root, it may also get moved around
    // as the component is being moved.
    (b.key != null || A && b === A.subTree) && Fl(
      m,
      b,
      !0
      /* shallow */
    )) : B(
      m,
      b,
      C,
      K,
      A,
      q,
      W,
      V,
      H
    );
  }, U = (m, b, C, $, A, q, W, V, H) => {
    b.slotScopeIds = V, m == null ? b.shapeFlag & 512 ? A.ctx.activate(
      b,
      C,
      $,
      W,
      H
    ) : x(
      b,
      C,
      $,
      A,
      q,
      W,
      H
    ) : D(m, b, H);
  }, x = (m, b, C, $, A, q, W) => {
    const V = m.component = ka(
      m,
      $,
      A
    );
    if (Ml(m) && (V.ctx.renderer = Zt), Ma(V), V.asyncDep) {
      if (A && A.registerDep(V, N), !m.el) {
        const H = V.subTree = _e(Bn);
        d(null, H, b, C);
      }
    } else
      N(
        V,
        m,
        b,
        C,
        A,
        q,
        W
      );
  }, D = (m, b, C) => {
    const $ = b.component = m.component;
    if (Ic(m, b, C))
      if ($.asyncDep && !$.asyncResolved) {
        j($, b, C);
        return;
      } else
        $.next = b, Mc($.update), $.effect.dirty = !0, $.update();
    else
      b.el = m.el, $.vnode = b;
  }, N = (m, b, C, $, A, q, W) => {
    const V = () => {
      if (m.isMounted) {
        let { next: K, bu: tt, u: Z, parent: nt, vnode: ct } = m;
        {
          const xe = jl(m);
          if (xe) {
            K && (K.el = ct.el, j(m, K, W)), xe.asyncDep.then(() => {
              m.isUnmounted || V();
            });
            return;
          }
        }
        let ht = K, vt;
        Ge(m, !1), K ? (K.el = ct.el, j(m, K, W)) : K = ct, tt && ui(tt), (vt = K.props && K.props.onVnodeBeforeUpdate) && pe(vt, nt, K, ct), Ge(m, !0);
        const Rt = ai(m), Vt = m.subTree;
        m.subTree = Rt, _(
          Vt,
          Rt,
          // parent may have changed if it's in a teleport
          f(Vt.el),
          // anchor may have changed if it's in a fragment
          xt(Vt),
          m,
          A,
          q
        ), K.el = Rt.el, ht === null && $c(m, Rt.el), Z && Gt(Z, A), (vt = K.props && K.props.onVnodeUpdated) && Gt(
          () => pe(vt, nt, K, ct),
          A
        );
      } else {
        let K;
        const { el: tt, props: Z } = b, { bm: nt, m: ct, parent: ht } = m, vt = yr(b);
        if (Ge(m, !1), nt && ui(nt), !vt && (K = Z && Z.onVnodeBeforeMount) && pe(K, ht, b), Ge(m, !0), tt && De) {
          const Rt = () => {
            m.subTree = ai(m), De(
              tt,
              m.subTree,
              m,
              A,
              null
            );
          };
          vt ? b.type.__asyncLoader().then(
            // note: we are moving the render call into an async callback,
            // which means it won't track dependencies - but it's ok because
            // a server-rendered async wrapper is already in resolved state
            // and it will never need to change.
            () => !m.isUnmounted && Rt()
          ) : Rt();
        } else {
          const Rt = m.subTree = ai(m);
          _(
            null,
            Rt,
            C,
            $,
            m,
            A,
            q
          ), b.el = Rt.el;
        }
        if (ct && Gt(ct, A), !vt && (K = Z && Z.onVnodeMounted)) {
          const Rt = b;
          Gt(
            () => pe(K, ht, Rt),
            A
          );
        }
        (b.shapeFlag & 256 || ht && yr(ht.vnode) && ht.vnode.shapeFlag & 256) && m.a && Gt(m.a, A), m.isMounted = !0, b = C = $ = null;
      }
    }, H = m.effect = new ns(
      V,
      ie,
      () => as(F),
      m.scope
      // track it in component's effect scope
    ), F = m.update = () => {
      H.dirty && H.run();
    };
    F.id = m.uid, Ge(m, !0), F();
  }, j = (m, b, C) => {
    b.component = m;
    const $ = m.vnode.props;
    m.vnode = b, m.next = null, la(m, b.props, $, C), aa(m, b.children, C), en(), Gs(m), nn();
  }, B = (m, b, C, $, A, q, W, V, H = !1) => {
    const F = m && m.children, K = m ? m.shapeFlag : 0, tt = b.children, { patchFlag: Z, shapeFlag: nt } = b;
    if (Z > 0) {
      if (Z & 128) {
        J(
          F,
          tt,
          C,
          $,
          A,
          q,
          W,
          V,
          H
        );
        return;
      } else if (Z & 256) {
        Y(
          F,
          tt,
          C,
          $,
          A,
          q,
          W,
          V,
          H
        );
        return;
      }
    }
    nt & 8 ? (K & 16 && mt(F, A, q), tt !== F && a(C, tt)) : K & 16 ? nt & 16 ? J(
      F,
      tt,
      C,
      $,
      A,
      q,
      W,
      V,
      H
    ) : mt(F, A, q, !0) : (K & 8 && a(C, ""), nt & 16 && G(
      tt,
      C,
      $,
      A,
      q,
      W,
      V,
      H
    ));
  }, Y = (m, b, C, $, A, q, W, V, H) => {
    m = m || un, b = b || un;
    const F = m.length, K = b.length, tt = Math.min(F, K);
    let Z;
    for (Z = 0; Z < tt; Z++) {
      const nt = b[Z] = H ? Oe(b[Z]) : me(b[Z]);
      _(
        m[Z],
        nt,
        C,
        null,
        A,
        q,
        W,
        V,
        H
      );
    }
    F > K ? mt(
      m,
      A,
      q,
      !0,
      !1,
      tt
    ) : G(
      b,
      C,
      $,
      A,
      q,
      W,
      V,
      H,
      tt
    );
  }, J = (m, b, C, $, A, q, W, V, H) => {
    let F = 0;
    const K = b.length;
    let tt = m.length - 1, Z = K - 1;
    for (; F <= tt && F <= Z; ) {
      const nt = m[F], ct = b[F] = H ? Oe(b[F]) : me(b[F]);
      if (xn(nt, ct))
        _(
          nt,
          ct,
          C,
          null,
          A,
          q,
          W,
          V,
          H
        );
      else
        break;
      F++;
    }
    for (; F <= tt && F <= Z; ) {
      const nt = m[tt], ct = b[Z] = H ? Oe(b[Z]) : me(b[Z]);
      if (xn(nt, ct))
        _(
          nt,
          ct,
          C,
          null,
          A,
          q,
          W,
          V,
          H
        );
      else
        break;
      tt--, Z--;
    }
    if (F > tt) {
      if (F <= Z) {
        const nt = Z + 1, ct = nt < K ? b[nt].el : $;
        for (; F <= Z; )
          _(
            null,
            b[F] = H ? Oe(b[F]) : me(b[F]),
            C,
            ct,
            A,
            q,
            W,
            V,
            H
          ), F++;
      }
    } else if (F > Z)
      for (; F <= tt; )
        lt(m[F], A, q, !0), F++;
    else {
      const nt = F, ct = F, ht = /* @__PURE__ */ new Map();
      for (F = ct; F <= Z; F++) {
        const It = b[F] = H ? Oe(b[F]) : me(b[F]);
        It.key != null && ht.set(It.key, F);
      }
      let vt, Rt = 0;
      const Vt = Z - ct + 1;
      let xe = !1, Jn = 0;
      const Ve = new Array(Vt);
      for (F = 0; F < Vt; F++)
        Ve[F] = 0;
      for (F = nt; F <= tt; F++) {
        const It = m[F];
        if (Rt >= Vt) {
          lt(It, A, q, !0);
          continue;
        }
        let te;
        if (It.key != null)
          te = ht.get(It.key);
        else
          for (vt = ct; vt <= Z; vt++)
            if (Ve[vt - ct] === 0 && xn(It, b[vt])) {
              te = vt;
              break;
            }
        te === void 0 ? lt(It, A, q, !0) : (Ve[te - ct] = F + 1, te >= Jn ? Jn = te : xe = !0, _(
          It,
          b[te],
          C,
          null,
          A,
          q,
          W,
          V,
          H
        ), Rt++);
      }
      const Qn = xe ? pa(Ve) : un;
      for (vt = Qn.length - 1, F = Vt - 1; F >= 0; F--) {
        const It = ct + F, te = b[It], Zn = It + 1 < K ? b[It + 1].el : $;
        Ve[F] === 0 ? _(
          null,
          te,
          C,
          Zn,
          A,
          q,
          W,
          V,
          H
        ) : xe && (vt < 0 || F !== Qn[vt] ? it(te, C, Zn, 2) : vt--);
      }
    }
  }, it = (m, b, C, $, A = null) => {
    const { el: q, type: W, transition: V, children: H, shapeFlag: F } = m;
    if (F & 6) {
      it(m.component.subTree, b, C, $);
      return;
    }
    if (F & 128) {
      m.suspense.move(b, C, $);
      return;
    }
    if (F & 64) {
      W.move(m, b, C, Zt);
      return;
    }
    if (W === re) {
      r(q, b, C);
      for (let tt = 0; tt < H.length; tt++)
        it(H[tt], b, C, $);
      r(m.anchor, b, C);
      return;
    }
    if (W === pi) {
      P(m, b, C);
      return;
    }
    if ($ !== 2 && F & 1 && V)
      if ($ === 0)
        V.beforeEnter(q), r(q, b, C), Gt(() => V.enter(q), A);
      else {
        const { leave: tt, delayLeave: Z, afterLeave: nt } = V, ct = () => r(q, b, C), ht = () => {
          tt(q, () => {
            ct(), nt && nt();
          });
        };
        Z ? Z(q, ct, ht) : ht();
      }
    else
      r(q, b, C);
  }, lt = (m, b, C, $ = !1, A = !1) => {
    const {
      type: q,
      props: W,
      ref: V,
      children: H,
      dynamicChildren: F,
      shapeFlag: K,
      patchFlag: tt,
      dirs: Z
    } = m;
    if (V != null && Oi(V, null, C, m, !0), K & 256) {
      b.ctx.deactivate(m);
      return;
    }
    const nt = K & 1 && Z, ct = !yr(m);
    let ht;
    if (ct && (ht = W && W.onVnodeBeforeUnmount) && pe(ht, b, m), K & 6)
      gt(m.component, C, $);
    else {
      if (K & 128) {
        m.suspense.unmount(C, $);
        return;
      }
      nt && qe(m, null, b, "beforeUnmount"), K & 64 ? m.type.remove(
        m,
        b,
        C,
        A,
        Zt,
        $
      ) : F && // #1153: fast path should not be taken for non-stable (v-for) fragments
      (q !== re || tt > 0 && tt & 64) ? mt(
        F,
        b,
        C,
        !1,
        !0
      ) : (q === re && tt & 384 || !A && K & 16) && mt(H, b, C), $ && kt(m);
    }
    (ct && (ht = W && W.onVnodeUnmounted) || nt) && Gt(() => {
      ht && pe(ht, b, m), nt && qe(m, null, b, "unmounted");
    }, C);
  }, kt = (m) => {
    const { type: b, el: C, anchor: $, transition: A } = m;
    if (b === re) {
      pt(C, $);
      return;
    }
    if (b === pi) {
      y(m);
      return;
    }
    const q = () => {
      i(C), A && !A.persisted && A.afterLeave && A.afterLeave();
    };
    if (m.shapeFlag & 1 && A && !A.persisted) {
      const { leave: W, delayLeave: V } = A, H = () => W(C, q);
      V ? V(m.el, q, H) : H();
    } else
      q();
  }, pt = (m, b) => {
    let C;
    for (; m !== b; )
      C = h(m), i(m), m = C;
    i(b);
  }, gt = (m, b, C) => {
    const { bum: $, scope: A, update: q, subTree: W, um: V } = m;
    $ && ui($), A.stop(), q && (q.active = !1, lt(W, m, b, C)), V && Gt(V, b), Gt(() => {
      m.isUnmounted = !0;
    }, b), b && b.pendingBranch && !b.isUnmounted && m.asyncDep && !m.asyncResolved && m.suspenseId === b.pendingId && (b.deps--, b.deps === 0 && b.resolve());
  }, mt = (m, b, C, $ = !1, A = !1, q = 0) => {
    for (let W = q; W < m.length; W++)
      lt(m[W], b, C, $, A);
  }, xt = (m) => m.shapeFlag & 6 ? xt(m.component.subTree) : m.shapeFlag & 128 ? m.suspense.next() : h(m.anchor || m.el);
  let Mt = !1;
  const Dt = (m, b, C) => {
    m == null ? b._vnode && lt(b._vnode, null, null, !0) : _(
      b._vnode || null,
      m,
      b,
      null,
      null,
      null,
      C
    ), Mt || (Mt = !0, Gs(), vl(), Mt = !1), b._vnode = m;
  }, Zt = {
    p: _,
    um: lt,
    m: it,
    r: kt,
    mt: x,
    mc: G,
    pc: B,
    pbc: X,
    n: xt,
    o: t
  };
  let de, De;
  return {
    render: Dt,
    hydrate: de,
    createApp: ia(Dt, de)
  };
}
function di({ type: t, props: e }, n) {
  return n === "svg" && t === "foreignObject" || n === "mathml" && t === "annotation-xml" && e && e.encoding && e.encoding.includes("html") ? void 0 : n;
}
function Ge({ effect: t, update: e }, n) {
  t.allowRecurse = e.allowRecurse = n;
}
function da(t, e) {
  return (!t || t && !t.pendingBranch) && e && !e.persisted;
}
function Fl(t, e, n = !1) {
  const r = t.children, i = e.children;
  if (rt(r) && rt(i))
    for (let s = 0; s < r.length; s++) {
      const o = r[s];
      let l = i[s];
      l.shapeFlag & 1 && !l.dynamicChildren && ((l.patchFlag <= 0 || l.patchFlag === 32) && (l = i[s] = Oe(i[s]), l.el = o.el), n || Fl(o, l)), l.type === Jr && (l.el = o.el);
    }
}
function pa(t) {
  const e = t.slice(), n = [0];
  let r, i, s, o, l;
  const u = t.length;
  for (r = 0; r < u; r++) {
    const c = t[r];
    if (c !== 0) {
      if (i = n[n.length - 1], t[i] < c) {
        e[r] = i, n.push(r);
        continue;
      }
      for (s = 0, o = n.length - 1; s < o; )
        l = s + o >> 1, t[n[l]] < c ? s = l + 1 : o = l;
      c < t[n[s]] && (s > 0 && (e[r] = n[s - 1]), n[s] = r);
    }
  }
  for (s = n.length, o = n[s - 1]; s-- > 0; )
    n[s] = o, o = e[o];
  return n;
}
function jl(t) {
  const e = t.subTree.component;
  if (e)
    return e.asyncDep && !e.asyncResolved ? e : jl(e);
}
const ga = (t) => t.__isTeleport, re = Symbol.for("v-fgt"), Jr = Symbol.for("v-txt"), Bn = Symbol.for("v-cmt"), pi = Symbol.for("v-stc"), In = [];
let ae = null;
function Pe(t = !1) {
  In.push(ae = t ? null : []);
}
function ma() {
  In.pop(), ae = In[In.length - 1] || null;
}
let zn = 1;
function eo(t) {
  zn += t;
}
function wa(t) {
  return t.dynamicChildren = zn > 0 ? ae || un : null, ma(), zn > 0 && ae && ae.push(t), t;
}
function Le(t, e, n, r, i, s) {
  return wa(
    Yt(
      t,
      e,
      n,
      r,
      i,
      s,
      !0
    )
  );
}
function ya(t) {
  return t ? t.__v_isVNode === !0 : !1;
}
function xn(t, e) {
  return t.type === e.type && t.key === e.key;
}
const Qr = "__vInternal", Bl = ({ key: t }) => t ?? null, vr = ({
  ref: t,
  ref_key: e,
  ref_for: n
}) => (typeof t == "number" && (t = "" + t), t != null ? Ct(t) || Kt(t) || ut(t) ? { i: Jt, r: t, k: e, f: !!n } : t : null);
function Yt(t, e = null, n = null, r = 0, i = null, s = t === re ? 0 : 1, o = !1, l = !1) {
  const u = {
    __v_isVNode: !0,
    __v_skip: !0,
    type: t,
    props: e,
    key: e && Bl(e),
    ref: e && vr(e),
    scopeId: El,
    slotScopeIds: null,
    children: n,
    component: null,
    suspense: null,
    ssContent: null,
    ssFallback: null,
    dirs: null,
    transition: null,
    el: null,
    anchor: null,
    target: null,
    targetAnchor: null,
    staticCount: 0,
    shapeFlag: s,
    patchFlag: r,
    dynamicProps: i,
    dynamicChildren: null,
    appContext: null,
    ctx: Jt
  };
  return l ? (gs(u, n), s & 128 && t.normalize(u)) : n && (u.shapeFlag |= Ct(n) ? 8 : 16), zn > 0 && // avoid a block node from tracking itself
  !o && // has current parent block
  ae && // presence of a patch flag indicates this node needs patching on updates.
  // component nodes also should always be patched, because even if the
  // component doesn't need to update, it needs to persist the instance on to
  // the next vnode so that it can be properly unmounted later.
  (u.patchFlag > 0 || s & 6) && // the EVENTS flag is only for hydration and if it is the only flag, the
  // vnode should not be considered dynamic due to handler caching.
  u.patchFlag !== 32 && ae.push(u), u;
}
const _e = _a;
function _a(t, e = null, n = null, r = 0, i = null, s = !1) {
  if ((!t || t === Oc) && (t = Bn), ya(t)) {
    const l = mn(
      t,
      e,
      !0
      /* mergeRef: true */
    );
    return n && gs(l, n), zn > 0 && !s && ae && (l.shapeFlag & 6 ? ae[ae.indexOf(t)] = l : ae.push(l)), l.patchFlag |= -2, l;
  }
  if (Ca(t) && (t = t.__vccOpts), e) {
    e = va(e);
    let { class: l, style: u } = e;
    l && !Ct(l) && (e.class = es(l)), St(u) && (hl(u) && !rt(u) && (u = Tt({}, u)), e.style = ts(u));
  }
  const o = Ct(t) ? 1 : Ac(t) ? 128 : ga(t) ? 64 : St(t) ? 4 : ut(t) ? 2 : 0;
  return Yt(
    t,
    e,
    n,
    r,
    i,
    o,
    s,
    !0
  );
}
function va(t) {
  return t ? hl(t) || Qr in t ? Tt({}, t) : t : null;
}
function mn(t, e, n = !1) {
  const { props: r, ref: i, patchFlag: s, children: o } = t, l = e ? xa(r || {}, e) : r;
  return {
    __v_isVNode: !0,
    __v_skip: !0,
    type: t.type,
    props: l,
    key: l && Bl(l),
    ref: e && e.ref ? (
      // #2078 in the case of <component :is="vnode" ref="extra"/>
      // if the vnode itself already has a ref, cloneVNode will need to merge
      // the refs so the single vnode can be set on multiple refs
      n && i ? rt(i) ? i.concat(vr(e)) : [i, vr(e)] : vr(e)
    ) : i,
    scopeId: t.scopeId,
    slotScopeIds: t.slotScopeIds,
    children: o,
    target: t.target,
    targetAnchor: t.targetAnchor,
    staticCount: t.staticCount,
    shapeFlag: t.shapeFlag,
    // if the vnode is cloned with extra props, we can no longer assume its
    // existing patch flag to be reliable and need to add the FULL_PROPS flag.
    // note: preserve flag for fragments since they use the flag for children
    // fast paths only.
    patchFlag: e && t.type !== re ? s === -1 ? 16 : s | 16 : s,
    dynamicProps: t.dynamicProps,
    dynamicChildren: t.dynamicChildren,
    appContext: t.appContext,
    dirs: t.dirs,
    transition: t.transition,
    // These should technically only be non-null on mounted VNodes. However,
    // they *should* be copied for kept-alive vnodes. So we just always copy
    // them since them being non-null during a mount doesn't affect the logic as
    // they will simply be overwritten.
    component: t.component,
    suspense: t.suspense,
    ssContent: t.ssContent && mn(t.ssContent),
    ssFallback: t.ssFallback && mn(t.ssFallback),
    el: t.el,
    anchor: t.anchor,
    ctx: t.ctx,
    ce: t.ce
  };
}
function ba(t = " ", e = 0) {
  return _e(Jr, null, t, e);
}
function me(t) {
  return t == null || typeof t == "boolean" ? _e(Bn) : rt(t) ? _e(
    re,
    null,
    // #3666, avoid reference pollution when reusing vnode
    t.slice()
  ) : typeof t == "object" ? Oe(t) : _e(Jr, null, String(t));
}
function Oe(t) {
  return t.el === null && t.patchFlag !== -1 || t.memo ? t : mn(t);
}
function gs(t, e) {
  let n = 0;
  const { shapeFlag: r } = t;
  if (e == null)
    e = null;
  else if (rt(e))
    n = 16;
  else if (typeof e == "object")
    if (r & 65) {
      const i = e.default;
      i && (i._c && (i._d = !1), gs(t, i()), i._c && (i._d = !0));
      return;
    } else {
      n = 32;
      const i = e._;
      !i && !(Qr in e) ? e._ctx = Jt : i === 3 && Jt && (Jt.slots._ === 1 ? e._ = 1 : (e._ = 2, t.patchFlag |= 1024));
    }
  else ut(e) ? (e = { default: e, _ctx: Jt }, n = 32) : (e = String(e), r & 64 ? (n = 16, e = [ba(e)]) : n = 8);
  t.children = e, t.shapeFlag |= n;
}
function xa(...t) {
  const e = {};
  for (let n = 0; n < t.length; n++) {
    const r = t[n];
    for (const i in r)
      if (i === "class")
        e.class !== r.class && (e.class = es([e.class, r.class]));
      else if (i === "style")
        e.style = ts([e.style, r.style]);
      else if (qr(i)) {
        const s = e[i], o = r[i];
        o && s !== o && !(rt(s) && s.includes(o)) && (e[i] = s ? [].concat(s, o) : o);
      } else i !== "" && (e[i] = r[i]);
  }
  return e;
}
function pe(t, e, n, r = null) {
  fe(t, e, 7, [
    n,
    r
  ]);
}
const Ea = Pl();
let Sa = 0;
function ka(t, e, n) {
  const r = t.type, i = (e ? e.appContext : t.appContext) || Ea, s = {
    uid: Sa++,
    vnode: t,
    type: r,
    parent: e,
    appContext: i,
    root: null,
    // to be immediately set
    next: null,
    subTree: null,
    // will be set synchronously right after creation
    effect: null,
    update: null,
    // will be set synchronously right after creation
    scope: new Ju(
      !0
      /* detached */
    ),
    render: null,
    proxy: null,
    exposed: null,
    exposeProxy: null,
    withProxy: null,
    provides: e ? e.provides : Object.create(i.provides),
    accessCache: null,
    renderCache: [],
    // local resolved assets
    components: null,
    directives: null,
    // resolved props and emits options
    propsOptions: Il(r, i),
    emitsOptions: xl(r, i),
    // emit
    emit: null,
    // to be set immediately
    emitted: null,
    // props default value
    propsDefaults: bt,
    // inheritAttrs
    inheritAttrs: r.inheritAttrs,
    // state
    ctx: bt,
    data: bt,
    props: bt,
    attrs: bt,
    slots: bt,
    refs: bt,
    setupState: bt,
    setupContext: null,
    attrsProxy: null,
    slotsProxy: null,
    // suspense related
    suspense: n,
    suspenseId: n ? n.pendingId : 0,
    asyncDep: null,
    asyncResolved: !1,
    // lifecycle hooks
    // not using enums here because it results in computed properties
    isMounted: !1,
    isUnmounted: !1,
    isDeactivated: !1,
    bc: null,
    c: null,
    bm: null,
    m: null,
    bu: null,
    u: null,
    um: null,
    bum: null,
    da: null,
    a: null,
    rtg: null,
    rtc: null,
    ec: null,
    sp: null
  };
  return s.ctx = { _: s }, s.root = e ? e.root : s, s.emit = Tc.bind(null, s), t.ce && t.ce(s), s;
}
let Ft = null, Lr, Ai;
{
  const t = Jo(), e = (n, r) => {
    let i;
    return (i = t[n]) || (i = t[n] = []), i.push(r), (s) => {
      i.length > 1 ? i.forEach((o) => o(s)) : i[0](s);
    };
  };
  Lr = e(
    "__VUE_INSTANCE_SETTERS__",
    (n) => Ft = n
  ), Ai = e(
    "__VUE_SSR_SETTERS__",
    (n) => Zr = n
  );
}
const Wn = (t) => {
  const e = Ft;
  return Lr(t), t.scope.on(), () => {
    t.scope.off(), Lr(e);
  };
}, no = () => {
  Ft && Ft.scope.off(), Lr(null);
};
function zl(t) {
  return t.vnode.shapeFlag & 4;
}
let Zr = !1;
function Ma(t, e = !1) {
  e && Ai(e);
  const { props: n, children: r } = t.vnode, i = zl(t);
  oa(t, n, i, e), ca(t, r);
  const s = i ? Na(t, e) : void 0;
  return e && Ai(!1), s;
}
function Na(t, e) {
  const n = t.type;
  t.accessCache = /* @__PURE__ */ Object.create(null), t.proxy = dl(new Proxy(t.ctx, Jc));
  const { setup: r } = n;
  if (r) {
    const i = t.setupContext = r.length > 1 ? Ta(t) : null, s = Wn(t);
    en();
    const o = Be(
      r,
      t,
      0,
      [
        t.props,
        i
      ]
    );
    if (nn(), s(), Wo(o)) {
      if (o.then(no, no), e)
        return o.then((l) => {
          ro(t, l, e);
        }).catch((l) => {
          Kr(l, t, 0);
        });
      t.asyncDep = o;
    } else
      ro(t, o, e);
  } else
    Dl(t, e);
}
function ro(t, e, n) {
  ut(e) ? t.type.__ssrInlineRender ? t.ssrRender = e : t.render = e : St(e) && (t.setupState = ml(e)), Dl(t, n);
}
let io;
function Dl(t, e, n) {
  const r = t.type;
  if (!t.render) {
    if (!e && io && !r.render) {
      const i = r.template || ds(t).template;
      if (i) {
        const { isCustomElement: s, compilerOptions: o } = t.appContext.config, { delimiters: l, compilerOptions: u } = r, c = Tt(
          Tt(
            {
              isCustomElement: s,
              delimiters: l
            },
            o
          ),
          u
        );
        r.render = io(i, c);
      }
    }
    t.render = r.render || ie;
  }
  {
    const i = Wn(t);
    en();
    try {
      Qc(t);
    } finally {
      nn(), i();
    }
  }
}
function Ra(t) {
  return t.attrsProxy || (t.attrsProxy = new Proxy(
    t.attrs,
    {
      get(e, n) {
        return Wt(t, "get", "$attrs"), e[n];
      }
    }
  ));
}
function Ta(t) {
  const e = (n) => {
    t.exposed = n || {};
  };
  return {
    get attrs() {
      return Ra(t);
    },
    slots: t.slots,
    emit: t.emit,
    expose: e
  };
}
function ti(t) {
  if (t.exposed)
    return t.exposeProxy || (t.exposeProxy = new Proxy(ml(dl(t.exposed)), {
      get(e, n) {
        if (n in e)
          return e[n];
        if (n in Pn)
          return Pn[n](t);
      },
      has(e, n) {
        return n in e || n in Pn;
      }
    }));
}
function Ca(t) {
  return ut(t) && "__vccOpts" in t;
}
const Fi = (t, e) => vc(t, e, Zr), Pa = "3.4.21";
/**
* @vue/runtime-dom v3.4.21
* (c) 2018-present Yuxi (Evan) You and Vue contributors
* @license MIT
**/
const La = "http://www.w3.org/2000/svg", Ia = "http://www.w3.org/1998/Math/MathML", Ae = typeof document < "u" ? document : null, so = Ae && /* @__PURE__ */ Ae.createElement("template"), $a = {
  insert: (t, e, n) => {
    e.insertBefore(t, n || null);
  },
  remove: (t) => {
    const e = t.parentNode;
    e && e.removeChild(t);
  },
  createElement: (t, e, n, r) => {
    const i = e === "svg" ? Ae.createElementNS(La, t) : e === "mathml" ? Ae.createElementNS(Ia, t) : Ae.createElement(t, n ? { is: n } : void 0);
    return t === "select" && r && r.multiple != null && i.setAttribute("multiple", r.multiple), i;
  },
  createText: (t) => Ae.createTextNode(t),
  createComment: (t) => Ae.createComment(t),
  setText: (t, e) => {
    t.nodeValue = e;
  },
  setElementText: (t, e) => {
    t.textContent = e;
  },
  parentNode: (t) => t.parentNode,
  nextSibling: (t) => t.nextSibling,
  querySelector: (t) => Ae.querySelector(t),
  setScopeId(t, e) {
    t.setAttribute(e, "");
  },
  // __UNSAFE__
  // Reason: innerHTML.
  // Static content here can only come from compiled templates.
  // As long as the user only uses trusted templates, this is safe.
  insertStaticContent(t, e, n, r, i, s) {
    const o = n ? n.previousSibling : e.lastChild;
    if (i && (i === s || i.nextSibling))
      for (; e.insertBefore(i.cloneNode(!0), n), !(i === s || !(i = i.nextSibling)); )
        ;
    else {
      so.innerHTML = r === "svg" ? `<svg>${t}</svg>` : r === "mathml" ? `<math>${t}</math>` : t;
      const l = so.content;
      if (r === "svg" || r === "mathml") {
        const u = l.firstChild;
        for (; u.firstChild; )
          l.appendChild(u.firstChild);
        l.removeChild(u);
      }
      e.insertBefore(l, n);
    }
    return [
      // first
      o ? o.nextSibling : e.firstChild,
      // last
      n ? n.previousSibling : e.lastChild
    ];
  }
}, Oa = Symbol("_vtc");
function Aa(t, e, n) {
  const r = t[Oa];
  r && (e = (e ? [e, ...r] : [...r]).join(" ")), e == null ? t.removeAttribute("class") : n ? t.setAttribute("class", e) : t.className = e;
}
const Ir = Symbol("_vod"), Vl = Symbol("_vsh"), br = {
  beforeMount(t, { value: e }, { transition: n }) {
    t[Ir] = t.style.display === "none" ? "" : t.style.display, n && e ? n.beforeEnter(t) : En(t, e);
  },
  mounted(t, { value: e }, { transition: n }) {
    n && e && n.enter(t);
  },
  updated(t, { value: e, oldValue: n }, { transition: r }) {
    !e != !n && (r ? e ? (r.beforeEnter(t), En(t, !0), r.enter(t)) : r.leave(t, () => {
      En(t, !1);
    }) : En(t, e));
  },
  beforeUnmount(t, { value: e }) {
    En(t, e);
  }
};
function En(t, e) {
  t.style.display = e ? t[Ir] : "none", t[Vl] = !e;
}
const Fa = Symbol(""), ja = /(^|;)\s*display\s*:/;
function Ba(t, e, n) {
  const r = t.style, i = Ct(n);
  let s = !1;
  if (n && !i) {
    if (e)
      if (Ct(e))
        for (const o of e.split(";")) {
          const l = o.slice(0, o.indexOf(":")).trim();
          n[l] == null && xr(r, l, "");
        }
      else
        for (const o in e)
          n[o] == null && xr(r, o, "");
    for (const o in n)
      o === "display" && (s = !0), xr(r, o, n[o]);
  } else if (i) {
    if (e !== n) {
      const o = r[Fa];
      o && (n += ";" + o), r.cssText = n, s = ja.test(n);
    }
  } else e && t.removeAttribute("style");
  Ir in t && (t[Ir] = s ? r.display : "", t[Vl] && (r.display = "none"));
}
const oo = /\s*!important$/;
function xr(t, e, n) {
  if (rt(n))
    n.forEach((r) => xr(t, e, r));
  else if (n == null && (n = ""), e.startsWith("--"))
    t.setProperty(e, n);
  else {
    const r = za(t, e);
    oo.test(n) ? t.setProperty(
      ue(r),
      n.replace(oo, ""),
      "important"
    ) : t[r] = n;
  }
}
const lo = ["Webkit", "Moz", "ms"], gi = {};
function za(t, e) {
  const n = gi[e];
  if (n)
    return n;
  let r = ke(e);
  if (r !== "filter" && r in t)
    return gi[e] = r;
  r = Yo(r);
  for (let i = 0; i < lo.length; i++) {
    const s = lo[i] + r;
    if (s in t)
      return gi[e] = s;
  }
  return e;
}
const uo = "http://www.w3.org/1999/xlink";
function Da(t, e, n, r, i) {
  if (r && e.startsWith("xlink:"))
    n == null ? t.removeAttributeNS(uo, e.slice(6, e.length)) : t.setAttributeNS(uo, e, n);
  else {
    const s = Yu(e);
    n == null || s && !Qo(n) ? t.removeAttribute(e) : t.setAttribute(e, s ? "" : n);
  }
}
function Va(t, e, n, r, i, s, o) {
  if (e === "innerHTML" || e === "textContent") {
    r && o(r, i, s), t[e] = n ?? "";
    return;
  }
  const l = t.tagName;
  if (e === "value" && l !== "PROGRESS" && // custom elements may use _value internally
  !l.includes("-")) {
    const c = l === "OPTION" ? t.getAttribute("value") || "" : t.value, a = n ?? "";
    (c !== a || !("_value" in t)) && (t.value = a), n == null && t.removeAttribute(e), t._value = n;
    return;
  }
  let u = !1;
  if (n === "" || n == null) {
    const c = typeof t[e];
    c === "boolean" ? n = Qo(n) : n == null && c === "string" ? (n = "", u = !0) : c === "number" && (n = 0, u = !0);
  }
  try {
    t[e] = n;
  } catch {
  }
  u && t.removeAttribute(e);
}
function qa(t, e, n, r) {
  t.addEventListener(e, n, r);
}
function Ga(t, e, n, r) {
  t.removeEventListener(e, n, r);
}
const co = Symbol("_vei");
function Ha(t, e, n, r, i = null) {
  const s = t[co] || (t[co] = {}), o = s[e];
  if (r && o)
    o.value = r;
  else {
    const [l, u] = Ua(e);
    if (r) {
      const c = s[e] = Xa(r, i);
      qa(t, l, c, u);
    } else o && (Ga(t, l, o, u), s[e] = void 0);
  }
}
const ao = /(?:Once|Passive|Capture)$/;
function Ua(t) {
  let e;
  if (ao.test(t)) {
    e = {};
    let r;
    for (; r = t.match(ao); )
      t = t.slice(0, t.length - r[0].length), e[r[0].toLowerCase()] = !0;
  }
  return [t[2] === ":" ? t.slice(3) : ue(t.slice(2)), e];
}
let mi = 0;
const Wa = /* @__PURE__ */ Promise.resolve(), Ka = () => mi || (Wa.then(() => mi = 0), mi = Date.now());
function Xa(t, e) {
  const n = (r) => {
    if (!r._vts)
      r._vts = Date.now();
    else if (r._vts <= n.attached)
      return;
    fe(
      Ya(r, n.value),
      e,
      5,
      [r]
    );
  };
  return n.value = t, n.attached = Ka(), n;
}
function Ya(t, e) {
  if (rt(e)) {
    const n = t.stopImmediatePropagation;
    return t.stopImmediatePropagation = () => {
      n.call(t), t._stopped = !0;
    }, e.map((r) => (i) => !i._stopped && r && r(i));
  } else
    return e;
}
const fo = (t) => t.charCodeAt(0) === 111 && t.charCodeAt(1) === 110 && // lowercase letter
t.charCodeAt(2) > 96 && t.charCodeAt(2) < 123, Ja = (t, e, n, r, i, s, o, l, u) => {
  const c = i === "svg";
  e === "class" ? Aa(t, r, c) : e === "style" ? Ba(t, n, r) : qr(e) ? Ji(e) || Ha(t, e, n, r, o) : (e[0] === "." ? (e = e.slice(1), !0) : e[0] === "^" ? (e = e.slice(1), !1) : Qa(t, e, r, c)) ? Va(
    t,
    e,
    r,
    s,
    o,
    l,
    u
  ) : (e === "true-value" ? t._trueValue = r : e === "false-value" && (t._falseValue = r), Da(t, e, r, c));
};
function Qa(t, e, n, r) {
  if (r)
    return !!(e === "innerHTML" || e === "textContent" || e in t && fo(e) && ut(n));
  if (e === "spellcheck" || e === "draggable" || e === "translate" || e === "form" || e === "list" && t.tagName === "INPUT" || e === "type" && t.tagName === "TEXTAREA")
    return !1;
  if (e === "width" || e === "height") {
    const i = t.tagName;
    if (i === "IMG" || i === "VIDEO" || i === "CANVAS" || i === "SOURCE")
      return !1;
  }
  return fo(e) && Ct(n) ? !1 : e in t;
}
const Za = /* @__PURE__ */ Tt({ patchProp: Ja }, $a);
let ho;
function tf() {
  return ho || (ho = fa(Za));
}
const po = (...t) => {
  tf().render(...t);
}, ef = { class: "graph-controller__controls-overview" }, nf = { key: 0 }, rf = { key: 1 }, sf = { key: 0 }, of = { key: 1 }, lf = /* @__PURE__ */ fs({
  __name: "GraphControls",
  props: {
    showHeader: { type: Boolean },
    showControlsGraph: { type: Boolean },
    showLatexInfo: { type: Boolean },
    showControlsEnvironment: { type: Boolean },
    platformType: {}
  },
  setup(t) {
    const e = t, n = [
      {
        action: "Create node",
        desktop: "Double-click",
        touch: "Double-tap"
      },
      {
        action: "Create link",
        desktop: "Right-click on node + hold + drag towards target",
        touch: "Hold + drag"
      },
      {
        action: "Delete node/link",
        desktop: "Right-click + hold",
        touch: "Touch + hold"
      },
      {
        action: "Move node",
        desktop: "Left-click + hold on node + drag",
        touch: "-"
      },
      {
        action: "Create/Update label",
        desktop: e.showLatexInfo ? "Left-click on label, $$ for $\\LaTeX$" : "Left-click on label",
        touch: e.showLatexInfo ? "Tap on label, $$ for $\\LaTeX$" : "Tap on label"
      }
    ], r = [
      {
        action: "Pan",
        desktop: "Left-click on canvas + hold + drag",
        touch: "Multi-touch"
      },
      {
        action: "Zoom",
        desktop: "Mouse wheel",
        touch: "Multi-touch"
      }
    ], i = ["Action", "Controls"];
    let s = e.platformType === "mobile" || e.platformType === "tablet";
    return (o, l) => (Pe(), Le("table", ef, [
      wr(Yt("thead", null, [
        Yt("tr", null, [
          Yt("th", null, Te(i[0]), 1),
          Yt("th", null, Te(i[1]), 1)
        ])
      ], 512), [
        [br, e.showHeader]
      ]),
      Yt("tbody", null, [
        (Pe(), Le(re, null, Us(n, (u) => wr(Yt("tr", {
          key: u.action
        }, [
          Yt("td", null, Te(u.action), 1),
          Tr(s) ? (Pe(), Le("td", nf, Te(u.touch), 1)) : (Pe(), Le("td", rf, Te(u.desktop), 1))
        ]), [
          [br, e.showControlsGraph]
        ])), 64)),
        (Pe(), Le(re, null, Us(r, (u) => wr(Yt("tr", {
          key: u.action
        }, [
          Yt("td", null, Te(u.action), 1),
          Tr(s) ? (Pe(), Le("td", sf, Te(u.touch), 1)) : (Pe(), Le("td", of, Te(u.desktop), 1))
        ]), [
          [br, e.showControlsEnvironment]
        ])), 64))
      ])
    ]));
  }
}), uf = (t, e) => {
  const n = t.__vccOpts || t;
  for (const [r, i] of e)
    n[r] = i;
  return n;
}, cf = /* @__PURE__ */ uf(lf, [["__scopeId", "data-v-8c3d818f"]]);
var af = { value: () => {
} };
function Kn() {
  for (var t = 0, e = arguments.length, n = {}, r; t < e; ++t) {
    if (!(r = arguments[t] + "") || r in n || /[\s.]/.test(r)) throw new Error("illegal type: " + r);
    n[r] = [];
  }
  return new Er(n);
}
function Er(t) {
  this._ = t;
}
function ff(t, e) {
  return t.trim().split(/^|\s+/).map(function(n) {
    var r = "", i = n.indexOf(".");
    if (i >= 0 && (r = n.slice(i + 1), n = n.slice(0, i)), n && !e.hasOwnProperty(n)) throw new Error("unknown type: " + n);
    return { type: n, name: r };
  });
}
Er.prototype = Kn.prototype = {
  constructor: Er,
  on: function(t, e) {
    var n = this._, r = ff(t + "", n), i, s = -1, o = r.length;
    if (arguments.length < 2) {
      for (; ++s < o; ) if ((i = (t = r[s]).type) && (i = hf(n[i], t.name))) return i;
      return;
    }
    if (e != null && typeof e != "function") throw new Error("invalid callback: " + e);
    for (; ++s < o; )
      if (i = (t = r[s]).type) n[i] = go(n[i], t.name, e);
      else if (e == null) for (i in n) n[i] = go(n[i], t.name, null);
    return this;
  },
  copy: function() {
    var t = {}, e = this._;
    for (var n in e) t[n] = e[n].slice();
    return new Er(t);
  },
  call: function(t, e) {
    if ((i = arguments.length - 2) > 0) for (var n = new Array(i), r = 0, i, s; r < i; ++r) n[r] = arguments[r + 2];
    if (!this._.hasOwnProperty(t)) throw new Error("unknown type: " + t);
    for (s = this._[t], r = 0, i = s.length; r < i; ++r) s[r].value.apply(e, n);
  },
  apply: function(t, e, n) {
    if (!this._.hasOwnProperty(t)) throw new Error("unknown type: " + t);
    for (var r = this._[t], i = 0, s = r.length; i < s; ++i) r[i].value.apply(e, n);
  }
};
function hf(t, e) {
  for (var n = 0, r = t.length, i; n < r; ++n)
    if ((i = t[n]).name === e)
      return i.value;
}
function go(t, e, n) {
  for (var r = 0, i = t.length; r < i; ++r)
    if (t[r].name === e) {
      t[r] = af, t = t.slice(0, r).concat(t.slice(r + 1));
      break;
    }
  return n != null && t.push({ name: e, value: n }), t;
}
var ji = "http://www.w3.org/1999/xhtml";
const mo = {
  svg: "http://www.w3.org/2000/svg",
  xhtml: ji,
  xlink: "http://www.w3.org/1999/xlink",
  xml: "http://www.w3.org/XML/1998/namespace",
  xmlns: "http://www.w3.org/2000/xmlns/"
};
function ei(t) {
  var e = t += "", n = e.indexOf(":");
  return n >= 0 && (e = t.slice(0, n)) !== "xmlns" && (t = t.slice(n + 1)), mo.hasOwnProperty(e) ? { space: mo[e], local: t } : t;
}
function df(t) {
  return function() {
    var e = this.ownerDocument, n = this.namespaceURI;
    return n === ji && e.documentElement.namespaceURI === ji ? e.createElement(t) : e.createElementNS(n, t);
  };
}
function pf(t) {
  return function() {
    return this.ownerDocument.createElementNS(t.space, t.local);
  };
}
function ql(t) {
  var e = ei(t);
  return (e.local ? pf : df)(e);
}
function gf() {
}
function ms(t) {
  return t == null ? gf : function() {
    return this.querySelector(t);
  };
}
function mf(t) {
  typeof t != "function" && (t = ms(t));
  for (var e = this._groups, n = e.length, r = new Array(n), i = 0; i < n; ++i)
    for (var s = e[i], o = s.length, l = r[i] = new Array(o), u, c, a = 0; a < o; ++a)
      (u = s[a]) && (c = t.call(u, u.__data__, a, s)) && ("__data__" in u && (c.__data__ = u.__data__), l[a] = c);
  return new Qt(r, this._parents);
}
function wf(t) {
  return t == null ? [] : Array.isArray(t) ? t : Array.from(t);
}
function yf() {
  return [];
}
function Gl(t) {
  return t == null ? yf : function() {
    return this.querySelectorAll(t);
  };
}
function _f(t) {
  return function() {
    return wf(t.apply(this, arguments));
  };
}
function vf(t) {
  typeof t == "function" ? t = _f(t) : t = Gl(t);
  for (var e = this._groups, n = e.length, r = [], i = [], s = 0; s < n; ++s)
    for (var o = e[s], l = o.length, u, c = 0; c < l; ++c)
      (u = o[c]) && (r.push(t.call(u, u.__data__, c, o)), i.push(u));
  return new Qt(r, i);
}
function Hl(t) {
  return function() {
    return this.matches(t);
  };
}
function Ul(t) {
  return function(e) {
    return e.matches(t);
  };
}
var bf = Array.prototype.find;
function xf(t) {
  return function() {
    return bf.call(this.children, t);
  };
}
function Ef() {
  return this.firstElementChild;
}
function Sf(t) {
  return this.select(t == null ? Ef : xf(typeof t == "function" ? t : Ul(t)));
}
var kf = Array.prototype.filter;
function Mf() {
  return Array.from(this.children);
}
function Nf(t) {
  return function() {
    return kf.call(this.children, t);
  };
}
function Rf(t) {
  return this.selectAll(t == null ? Mf : Nf(typeof t == "function" ? t : Ul(t)));
}
function Tf(t) {
  typeof t != "function" && (t = Hl(t));
  for (var e = this._groups, n = e.length, r = new Array(n), i = 0; i < n; ++i)
    for (var s = e[i], o = s.length, l = r[i] = [], u, c = 0; c < o; ++c)
      (u = s[c]) && t.call(u, u.__data__, c, s) && l.push(u);
  return new Qt(r, this._parents);
}
function Wl(t) {
  return new Array(t.length);
}
function Cf() {
  return new Qt(this._enter || this._groups.map(Wl), this._parents);
}
function $r(t, e) {
  this.ownerDocument = t.ownerDocument, this.namespaceURI = t.namespaceURI, this._next = null, this._parent = t, this.__data__ = e;
}
$r.prototype = {
  constructor: $r,
  appendChild: function(t) {
    return this._parent.insertBefore(t, this._next);
  },
  insertBefore: function(t, e) {
    return this._parent.insertBefore(t, e);
  },
  querySelector: function(t) {
    return this._parent.querySelector(t);
  },
  querySelectorAll: function(t) {
    return this._parent.querySelectorAll(t);
  }
};
function Pf(t) {
  return function() {
    return t;
  };
}
function Lf(t, e, n, r, i, s) {
  for (var o = 0, l, u = e.length, c = s.length; o < c; ++o)
    (l = e[o]) ? (l.__data__ = s[o], r[o] = l) : n[o] = new $r(t, s[o]);
  for (; o < u; ++o)
    (l = e[o]) && (i[o] = l);
}
function If(t, e, n, r, i, s, o) {
  var l, u, c = /* @__PURE__ */ new Map(), a = e.length, f = s.length, h = new Array(a), p;
  for (l = 0; l < a; ++l)
    (u = e[l]) && (h[l] = p = o.call(u, u.__data__, l, e) + "", c.has(p) ? i[l] = u : c.set(p, u));
  for (l = 0; l < f; ++l)
    p = o.call(t, s[l], l, s) + "", (u = c.get(p)) ? (r[l] = u, u.__data__ = s[l], c.delete(p)) : n[l] = new $r(t, s[l]);
  for (l = 0; l < a; ++l)
    (u = e[l]) && c.get(h[l]) === u && (i[l] = u);
}
function $f(t) {
  return t.__data__;
}
function Of(t, e) {
  if (!arguments.length) return Array.from(this, $f);
  var n = e ? If : Lf, r = this._parents, i = this._groups;
  typeof t != "function" && (t = Pf(t));
  for (var s = i.length, o = new Array(s), l = new Array(s), u = new Array(s), c = 0; c < s; ++c) {
    var a = r[c], f = i[c], h = f.length, p = Af(t.call(a, a && a.__data__, c, r)), w = p.length, _ = l[c] = new Array(w), v = o[c] = new Array(w), d = u[c] = new Array(h);
    n(a, f, _, v, d, p, e);
    for (var T = 0, P = 0, y, k; T < w; ++T)
      if (y = _[T]) {
        for (T >= P && (P = T + 1); !(k = v[P]) && ++P < w; ) ;
        y._next = k || null;
      }
  }
  return o = new Qt(o, r), o._enter = l, o._exit = u, o;
}
function Af(t) {
  return typeof t == "object" && "length" in t ? t : Array.from(t);
}
function Ff() {
  return new Qt(this._exit || this._groups.map(Wl), this._parents);
}
function jf(t, e, n) {
  var r = this.enter(), i = this, s = this.exit();
  return typeof t == "function" ? (r = t(r), r && (r = r.selection())) : r = r.append(t + ""), e != null && (i = e(i), i && (i = i.selection())), n == null ? s.remove() : n(s), r && i ? r.merge(i).order() : i;
}
function Bf(t) {
  for (var e = t.selection ? t.selection() : t, n = this._groups, r = e._groups, i = n.length, s = r.length, o = Math.min(i, s), l = new Array(i), u = 0; u < o; ++u)
    for (var c = n[u], a = r[u], f = c.length, h = l[u] = new Array(f), p, w = 0; w < f; ++w)
      (p = c[w] || a[w]) && (h[w] = p);
  for (; u < i; ++u)
    l[u] = n[u];
  return new Qt(l, this._parents);
}
function zf() {
  for (var t = this._groups, e = -1, n = t.length; ++e < n; )
    for (var r = t[e], i = r.length - 1, s = r[i], o; --i >= 0; )
      (o = r[i]) && (s && o.compareDocumentPosition(s) ^ 4 && s.parentNode.insertBefore(o, s), s = o);
  return this;
}
function Df(t) {
  t || (t = Vf);
  function e(f, h) {
    return f && h ? t(f.__data__, h.__data__) : !f - !h;
  }
  for (var n = this._groups, r = n.length, i = new Array(r), s = 0; s < r; ++s) {
    for (var o = n[s], l = o.length, u = i[s] = new Array(l), c, a = 0; a < l; ++a)
      (c = o[a]) && (u[a] = c);
    u.sort(e);
  }
  return new Qt(i, this._parents).order();
}
function Vf(t, e) {
  return t < e ? -1 : t > e ? 1 : t >= e ? 0 : NaN;
}
function qf() {
  var t = arguments[0];
  return arguments[0] = this, t.apply(null, arguments), this;
}
function Gf() {
  return Array.from(this);
}
function Hf() {
  for (var t = this._groups, e = 0, n = t.length; e < n; ++e)
    for (var r = t[e], i = 0, s = r.length; i < s; ++i) {
      var o = r[i];
      if (o) return o;
    }
  return null;
}
function Uf() {
  let t = 0;
  for (const e of this) ++t;
  return t;
}
function Wf() {
  return !this.node();
}
function Kf(t) {
  for (var e = this._groups, n = 0, r = e.length; n < r; ++n)
    for (var i = e[n], s = 0, o = i.length, l; s < o; ++s)
      (l = i[s]) && t.call(l, l.__data__, s, i);
  return this;
}
function Xf(t) {
  return function() {
    this.removeAttribute(t);
  };
}
function Yf(t) {
  return function() {
    this.removeAttributeNS(t.space, t.local);
  };
}
function Jf(t, e) {
  return function() {
    this.setAttribute(t, e);
  };
}
function Qf(t, e) {
  return function() {
    this.setAttributeNS(t.space, t.local, e);
  };
}
function Zf(t, e) {
  return function() {
    var n = e.apply(this, arguments);
    n == null ? this.removeAttribute(t) : this.setAttribute(t, n);
  };
}
function th(t, e) {
  return function() {
    var n = e.apply(this, arguments);
    n == null ? this.removeAttributeNS(t.space, t.local) : this.setAttributeNS(t.space, t.local, n);
  };
}
function eh(t, e) {
  var n = ei(t);
  if (arguments.length < 2) {
    var r = this.node();
    return n.local ? r.getAttributeNS(n.space, n.local) : r.getAttribute(n);
  }
  return this.each((e == null ? n.local ? Yf : Xf : typeof e == "function" ? n.local ? th : Zf : n.local ? Qf : Jf)(n, e));
}
function Kl(t) {
  return t.ownerDocument && t.ownerDocument.defaultView || t.document && t || t.defaultView;
}
function nh(t) {
  return function() {
    this.style.removeProperty(t);
  };
}
function rh(t, e, n) {
  return function() {
    this.style.setProperty(t, e, n);
  };
}
function ih(t, e, n) {
  return function() {
    var r = e.apply(this, arguments);
    r == null ? this.style.removeProperty(t) : this.style.setProperty(t, r, n);
  };
}
function sh(t, e, n) {
  return arguments.length > 1 ? this.each((e == null ? nh : typeof e == "function" ? ih : rh)(t, e, n ?? "")) : wn(this.node(), t);
}
function wn(t, e) {
  return t.style.getPropertyValue(e) || Kl(t).getComputedStyle(t, null).getPropertyValue(e);
}
function oh(t) {
  return function() {
    delete this[t];
  };
}
function lh(t, e) {
  return function() {
    this[t] = e;
  };
}
function uh(t, e) {
  return function() {
    var n = e.apply(this, arguments);
    n == null ? delete this[t] : this[t] = n;
  };
}
function ch(t, e) {
  return arguments.length > 1 ? this.each((e == null ? oh : typeof e == "function" ? uh : lh)(t, e)) : this.node()[t];
}
function Xl(t) {
  return t.trim().split(/^|\s+/);
}
function ws(t) {
  return t.classList || new Yl(t);
}
function Yl(t) {
  this._node = t, this._names = Xl(t.getAttribute("class") || "");
}
Yl.prototype = {
  add: function(t) {
    var e = this._names.indexOf(t);
    e < 0 && (this._names.push(t), this._node.setAttribute("class", this._names.join(" ")));
  },
  remove: function(t) {
    var e = this._names.indexOf(t);
    e >= 0 && (this._names.splice(e, 1), this._node.setAttribute("class", this._names.join(" ")));
  },
  contains: function(t) {
    return this._names.indexOf(t) >= 0;
  }
};
function Jl(t, e) {
  for (var n = ws(t), r = -1, i = e.length; ++r < i; ) n.add(e[r]);
}
function Ql(t, e) {
  for (var n = ws(t), r = -1, i = e.length; ++r < i; ) n.remove(e[r]);
}
function ah(t) {
  return function() {
    Jl(this, t);
  };
}
function fh(t) {
  return function() {
    Ql(this, t);
  };
}
function hh(t, e) {
  return function() {
    (e.apply(this, arguments) ? Jl : Ql)(this, t);
  };
}
function dh(t, e) {
  var n = Xl(t + "");
  if (arguments.length < 2) {
    for (var r = ws(this.node()), i = -1, s = n.length; ++i < s; ) if (!r.contains(n[i])) return !1;
    return !0;
  }
  return this.each((typeof e == "function" ? hh : e ? ah : fh)(n, e));
}
function ph() {
  this.textContent = "";
}
function gh(t) {
  return function() {
    this.textContent = t;
  };
}
function mh(t) {
  return function() {
    var e = t.apply(this, arguments);
    this.textContent = e ?? "";
  };
}
function wh(t) {
  return arguments.length ? this.each(t == null ? ph : (typeof t == "function" ? mh : gh)(t)) : this.node().textContent;
}
function yh() {
  this.innerHTML = "";
}
function _h(t) {
  return function() {
    this.innerHTML = t;
  };
}
function vh(t) {
  return function() {
    var e = t.apply(this, arguments);
    this.innerHTML = e ?? "";
  };
}
function bh(t) {
  return arguments.length ? this.each(t == null ? yh : (typeof t == "function" ? vh : _h)(t)) : this.node().innerHTML;
}
function xh() {
  this.nextSibling && this.parentNode.appendChild(this);
}
function Eh() {
  return this.each(xh);
}
function Sh() {
  this.previousSibling && this.parentNode.insertBefore(this, this.parentNode.firstChild);
}
function kh() {
  return this.each(Sh);
}
function Mh(t) {
  var e = typeof t == "function" ? t : ql(t);
  return this.select(function() {
    return this.appendChild(e.apply(this, arguments));
  });
}
function Nh() {
  return null;
}
function Rh(t, e) {
  var n = typeof t == "function" ? t : ql(t), r = e == null ? Nh : typeof e == "function" ? e : ms(e);
  return this.select(function() {
    return this.insertBefore(n.apply(this, arguments), r.apply(this, arguments) || null);
  });
}
function Th() {
  var t = this.parentNode;
  t && t.removeChild(this);
}
function Ch() {
  return this.each(Th);
}
function Ph() {
  var t = this.cloneNode(!1), e = this.parentNode;
  return e ? e.insertBefore(t, this.nextSibling) : t;
}
function Lh() {
  var t = this.cloneNode(!0), e = this.parentNode;
  return e ? e.insertBefore(t, this.nextSibling) : t;
}
function Ih(t) {
  return this.select(t ? Lh : Ph);
}
function $h(t) {
  return arguments.length ? this.property("__data__", t) : this.node().__data__;
}
function Oh(t) {
  return function(e) {
    t.call(this, e, this.__data__);
  };
}
function Ah(t) {
  return t.trim().split(/^|\s+/).map(function(e) {
    var n = "", r = e.indexOf(".");
    return r >= 0 && (n = e.slice(r + 1), e = e.slice(0, r)), { type: e, name: n };
  });
}
function Fh(t) {
  return function() {
    var e = this.__on;
    if (e) {
      for (var n = 0, r = -1, i = e.length, s; n < i; ++n)
        s = e[n], (!t.type || s.type === t.type) && s.name === t.name ? this.removeEventListener(s.type, s.listener, s.options) : e[++r] = s;
      ++r ? e.length = r : delete this.__on;
    }
  };
}
function jh(t, e, n) {
  return function() {
    var r = this.__on, i, s = Oh(e);
    if (r) {
      for (var o = 0, l = r.length; o < l; ++o)
        if ((i = r[o]).type === t.type && i.name === t.name) {
          this.removeEventListener(i.type, i.listener, i.options), this.addEventListener(i.type, i.listener = s, i.options = n), i.value = e;
          return;
        }
    }
    this.addEventListener(t.type, s, n), i = { type: t.type, name: t.name, value: e, listener: s, options: n }, r ? r.push(i) : this.__on = [i];
  };
}
function Bh(t, e, n) {
  var r = Ah(t + ""), i, s = r.length, o;
  if (arguments.length < 2) {
    var l = this.node().__on;
    if (l) {
      for (var u = 0, c = l.length, a; u < c; ++u)
        for (i = 0, a = l[u]; i < s; ++i)
          if ((o = r[i]).type === a.type && o.name === a.name)
            return a.value;
    }
    return;
  }
  for (l = e ? jh : Fh, i = 0; i < s; ++i) this.each(l(r[i], e, n));
  return this;
}
function Zl(t, e, n) {
  var r = Kl(t), i = r.CustomEvent;
  typeof i == "function" ? i = new i(e, n) : (i = r.document.createEvent("Event"), n ? (i.initEvent(e, n.bubbles, n.cancelable), i.detail = n.detail) : i.initEvent(e, !1, !1)), t.dispatchEvent(i);
}
function zh(t, e) {
  return function() {
    return Zl(this, t, e);
  };
}
function Dh(t, e) {
  return function() {
    return Zl(this, t, e.apply(this, arguments));
  };
}
function Vh(t, e) {
  return this.each((typeof e == "function" ? Dh : zh)(t, e));
}
function* qh() {
  for (var t = this._groups, e = 0, n = t.length; e < n; ++e)
    for (var r = t[e], i = 0, s = r.length, o; i < s; ++i)
      (o = r[i]) && (yield o);
}
var tu = [null];
function Qt(t, e) {
  this._groups = t, this._parents = e;
}
function Xn() {
  return new Qt([[document.documentElement]], tu);
}
function Gh() {
  return this;
}
Qt.prototype = Xn.prototype = {
  constructor: Qt,
  select: mf,
  selectAll: vf,
  selectChild: Sf,
  selectChildren: Rf,
  filter: Tf,
  data: Of,
  enter: Cf,
  exit: Ff,
  join: jf,
  merge: Bf,
  selection: Gh,
  order: zf,
  sort: Df,
  call: qf,
  nodes: Gf,
  node: Hf,
  size: Uf,
  empty: Wf,
  each: Kf,
  attr: eh,
  style: sh,
  property: ch,
  classed: dh,
  text: wh,
  html: bh,
  raise: Eh,
  lower: kh,
  append: Mh,
  insert: Rh,
  remove: Ch,
  clone: Ih,
  datum: $h,
  on: Bh,
  dispatch: Vh,
  [Symbol.iterator]: qh
};
function Et(t) {
  return typeof t == "string" ? new Qt([[document.querySelector(t)]], [document.documentElement]) : new Qt([[t]], tu);
}
function eu(t) {
  let e;
  for (; e = t.sourceEvent; ) t = e;
  return t;
}
function ne(t, e) {
  if (t = eu(t), e === void 0 && (e = t.currentTarget), e) {
    var n = e.ownerSVGElement || e;
    if (n.createSVGPoint) {
      var r = n.createSVGPoint();
      return r.x = t.clientX, r.y = t.clientY, r = r.matrixTransform(e.getScreenCTM().inverse()), [r.x, r.y];
    }
    if (e.getBoundingClientRect) {
      var i = e.getBoundingClientRect();
      return [t.clientX - i.left - e.clientLeft, t.clientY - i.top - e.clientTop];
    }
  }
  return [t.pageX, t.pageY];
}
function Hh(t, e) {
  return t.target && (t = eu(t), e === void 0 && (e = t.currentTarget), t = t.touches || [t]), Array.from(t, (n) => ne(n, e));
}
const Uh = { passive: !1 }, Dn = { capture: !0, passive: !1 };
function wi(t) {
  t.stopImmediatePropagation();
}
function hn(t) {
  t.preventDefault(), t.stopImmediatePropagation();
}
function nu(t) {
  var e = t.document.documentElement, n = Et(t).on("dragstart.drag", hn, Dn);
  "onselectstart" in e ? n.on("selectstart.drag", hn, Dn) : (e.__noselect = e.style.MozUserSelect, e.style.MozUserSelect = "none");
}
function ru(t, e) {
  var n = t.document.documentElement, r = Et(t).on("dragstart.drag", null);
  e && (r.on("click.drag", hn, Dn), setTimeout(function() {
    r.on("click.drag", null);
  }, 0)), "onselectstart" in n ? r.on("selectstart.drag", null) : (n.style.MozUserSelect = n.__noselect, delete n.__noselect);
}
const or = (t) => () => t;
function Bi(t, {
  sourceEvent: e,
  subject: n,
  target: r,
  identifier: i,
  active: s,
  x: o,
  y: l,
  dx: u,
  dy: c,
  dispatch: a
}) {
  Object.defineProperties(this, {
    type: { value: t, enumerable: !0, configurable: !0 },
    sourceEvent: { value: e, enumerable: !0, configurable: !0 },
    subject: { value: n, enumerable: !0, configurable: !0 },
    target: { value: r, enumerable: !0, configurable: !0 },
    identifier: { value: i, enumerable: !0, configurable: !0 },
    active: { value: s, enumerable: !0, configurable: !0 },
    x: { value: o, enumerable: !0, configurable: !0 },
    y: { value: l, enumerable: !0, configurable: !0 },
    dx: { value: u, enumerable: !0, configurable: !0 },
    dy: { value: c, enumerable: !0, configurable: !0 },
    _: { value: a }
  });
}
Bi.prototype.on = function() {
  var t = this._.on.apply(this._, arguments);
  return t === this._ ? this : t;
};
function Wh(t) {
  return !t.ctrlKey && !t.button;
}
function Kh() {
  return this.parentNode;
}
function Xh(t, e) {
  return e ?? { x: t.x, y: t.y };
}
function Yh() {
  return navigator.maxTouchPoints || "ontouchstart" in this;
}
function Jh() {
  var t = Wh, e = Kh, n = Xh, r = Yh, i = {}, s = Kn("start", "drag", "end"), o = 0, l, u, c, a, f = 0;
  function h(y) {
    y.on("mousedown.drag", p).filter(r).on("touchstart.drag", v).on("touchmove.drag", d, Uh).on("touchend.drag touchcancel.drag", T).style("touch-action", "none").style("-webkit-tap-highlight-color", "rgba(0,0,0,0)");
  }
  function p(y, k) {
    if (!(a || !t.call(this, y, k))) {
      var L = P(this, e.call(this, y, k), y, k, "mouse");
      L && (Et(y.view).on("mousemove.drag", w, Dn).on("mouseup.drag", _, Dn), nu(y.view), wi(y), c = !1, l = y.clientX, u = y.clientY, L("start", y));
    }
  }
  function w(y) {
    if (hn(y), !c) {
      var k = y.clientX - l, L = y.clientY - u;
      c = k * k + L * L > f;
    }
    i.mouse("drag", y);
  }
  function _(y) {
    Et(y.view).on("mousemove.drag mouseup.drag", null), ru(y.view, c), hn(y), i.mouse("end", y);
  }
  function v(y, k) {
    if (t.call(this, y, k)) {
      var L = y.changedTouches, z = e.call(this, y, k), G = L.length, Q, X;
      for (Q = 0; Q < G; ++Q)
        (X = P(this, z, y, k, L[Q].identifier, L[Q])) && (wi(y), X("start", y, L[Q]));
    }
  }
  function d(y) {
    var k = y.changedTouches, L = k.length, z, G;
    for (z = 0; z < L; ++z)
      (G = i[k[z].identifier]) && (hn(y), G("drag", y, k[z]));
  }
  function T(y) {
    var k = y.changedTouches, L = k.length, z, G;
    for (a && clearTimeout(a), a = setTimeout(function() {
      a = null;
    }, 500), z = 0; z < L; ++z)
      (G = i[k[z].identifier]) && (wi(y), G("end", y, k[z]));
  }
  function P(y, k, L, z, G, Q) {
    var X = s.copy(), et = ne(Q || L, k), ot, U, x;
    if ((x = n.call(y, new Bi("beforestart", {
      sourceEvent: L,
      target: h,
      identifier: G,
      active: o,
      x: et[0],
      y: et[1],
      dx: 0,
      dy: 0,
      dispatch: X
    }), z)) != null)
      return ot = x.x - et[0] || 0, U = x.y - et[1] || 0, function D(N, j, B) {
        var Y = et, J;
        switch (N) {
          case "start":
            i[G] = D, J = o++;
            break;
          case "end":
            delete i[G], --o;
          case "drag":
            et = ne(B || j, k), J = o;
            break;
        }
        X.call(
          N,
          y,
          new Bi(N, {
            sourceEvent: j,
            subject: x,
            target: h,
            identifier: G,
            active: J,
            x: et[0] + ot,
            y: et[1] + U,
            dx: et[0] - Y[0],
            dy: et[1] - Y[1],
            dispatch: X
          }),
          z
        );
      };
  }
  return h.filter = function(y) {
    return arguments.length ? (t = typeof y == "function" ? y : or(!!y), h) : t;
  }, h.container = function(y) {
    return arguments.length ? (e = typeof y == "function" ? y : or(y), h) : e;
  }, h.subject = function(y) {
    return arguments.length ? (n = typeof y == "function" ? y : or(y), h) : n;
  }, h.touchable = function(y) {
    return arguments.length ? (r = typeof y == "function" ? y : or(!!y), h) : r;
  }, h.on = function() {
    var y = s.on.apply(s, arguments);
    return y === s ? h : y;
  }, h.clickDistance = function(y) {
    return arguments.length ? (f = (y = +y) * y, h) : Math.sqrt(f);
  }, h;
}
function ys(t, e, n) {
  t.prototype = e.prototype = n, n.constructor = t;
}
function iu(t, e) {
  var n = Object.create(t.prototype);
  for (var r in e) n[r] = e[r];
  return n;
}
function Yn() {
}
var Vn = 0.7, Or = 1 / Vn, dn = "\\s*([+-]?\\d+)\\s*", qn = "\\s*([+-]?(?:\\d*\\.)?\\d+(?:[eE][+-]?\\d+)?)\\s*", ve = "\\s*([+-]?(?:\\d*\\.)?\\d+(?:[eE][+-]?\\d+)?)%\\s*", Qh = /^#([0-9a-f]{3,8})$/, Zh = new RegExp(`^rgb\\(${dn},${dn},${dn}\\)$`), td = new RegExp(`^rgb\\(${ve},${ve},${ve}\\)$`), ed = new RegExp(`^rgba\\(${dn},${dn},${dn},${qn}\\)$`), nd = new RegExp(`^rgba\\(${ve},${ve},${ve},${qn}\\)$`), rd = new RegExp(`^hsl\\(${qn},${ve},${ve}\\)$`), id = new RegExp(`^hsla\\(${qn},${ve},${ve},${qn}\\)$`), wo = {
  aliceblue: 15792383,
  antiquewhite: 16444375,
  aqua: 65535,
  aquamarine: 8388564,
  azure: 15794175,
  beige: 16119260,
  bisque: 16770244,
  black: 0,
  blanchedalmond: 16772045,
  blue: 255,
  blueviolet: 9055202,
  brown: 10824234,
  burlywood: 14596231,
  cadetblue: 6266528,
  chartreuse: 8388352,
  chocolate: 13789470,
  coral: 16744272,
  cornflowerblue: 6591981,
  cornsilk: 16775388,
  crimson: 14423100,
  cyan: 65535,
  darkblue: 139,
  darkcyan: 35723,
  darkgoldenrod: 12092939,
  darkgray: 11119017,
  darkgreen: 25600,
  darkgrey: 11119017,
  darkkhaki: 12433259,
  darkmagenta: 9109643,
  darkolivegreen: 5597999,
  darkorange: 16747520,
  darkorchid: 10040012,
  darkred: 9109504,
  darksalmon: 15308410,
  darkseagreen: 9419919,
  darkslateblue: 4734347,
  darkslategray: 3100495,
  darkslategrey: 3100495,
  darkturquoise: 52945,
  darkviolet: 9699539,
  deeppink: 16716947,
  deepskyblue: 49151,
  dimgray: 6908265,
  dimgrey: 6908265,
  dodgerblue: 2003199,
  firebrick: 11674146,
  floralwhite: 16775920,
  forestgreen: 2263842,
  fuchsia: 16711935,
  gainsboro: 14474460,
  ghostwhite: 16316671,
  gold: 16766720,
  goldenrod: 14329120,
  gray: 8421504,
  green: 32768,
  greenyellow: 11403055,
  grey: 8421504,
  honeydew: 15794160,
  hotpink: 16738740,
  indianred: 13458524,
  indigo: 4915330,
  ivory: 16777200,
  khaki: 15787660,
  lavender: 15132410,
  lavenderblush: 16773365,
  lawngreen: 8190976,
  lemonchiffon: 16775885,
  lightblue: 11393254,
  lightcoral: 15761536,
  lightcyan: 14745599,
  lightgoldenrodyellow: 16448210,
  lightgray: 13882323,
  lightgreen: 9498256,
  lightgrey: 13882323,
  lightpink: 16758465,
  lightsalmon: 16752762,
  lightseagreen: 2142890,
  lightskyblue: 8900346,
  lightslategray: 7833753,
  lightslategrey: 7833753,
  lightsteelblue: 11584734,
  lightyellow: 16777184,
  lime: 65280,
  limegreen: 3329330,
  linen: 16445670,
  magenta: 16711935,
  maroon: 8388608,
  mediumaquamarine: 6737322,
  mediumblue: 205,
  mediumorchid: 12211667,
  mediumpurple: 9662683,
  mediumseagreen: 3978097,
  mediumslateblue: 8087790,
  mediumspringgreen: 64154,
  mediumturquoise: 4772300,
  mediumvioletred: 13047173,
  midnightblue: 1644912,
  mintcream: 16121850,
  mistyrose: 16770273,
  moccasin: 16770229,
  navajowhite: 16768685,
  navy: 128,
  oldlace: 16643558,
  olive: 8421376,
  olivedrab: 7048739,
  orange: 16753920,
  orangered: 16729344,
  orchid: 14315734,
  palegoldenrod: 15657130,
  palegreen: 10025880,
  paleturquoise: 11529966,
  palevioletred: 14381203,
  papayawhip: 16773077,
  peachpuff: 16767673,
  peru: 13468991,
  pink: 16761035,
  plum: 14524637,
  powderblue: 11591910,
  purple: 8388736,
  rebeccapurple: 6697881,
  red: 16711680,
  rosybrown: 12357519,
  royalblue: 4286945,
  saddlebrown: 9127187,
  salmon: 16416882,
  sandybrown: 16032864,
  seagreen: 3050327,
  seashell: 16774638,
  sienna: 10506797,
  silver: 12632256,
  skyblue: 8900331,
  slateblue: 6970061,
  slategray: 7372944,
  slategrey: 7372944,
  snow: 16775930,
  springgreen: 65407,
  steelblue: 4620980,
  tan: 13808780,
  teal: 32896,
  thistle: 14204888,
  tomato: 16737095,
  turquoise: 4251856,
  violet: 15631086,
  wheat: 16113331,
  white: 16777215,
  whitesmoke: 16119285,
  yellow: 16776960,
  yellowgreen: 10145074
};
ys(Yn, Ze, {
  copy(t) {
    return Object.assign(new this.constructor(), this, t);
  },
  displayable() {
    return this.rgb().displayable();
  },
  hex: yo,
  // Deprecated! Use color.formatHex.
  formatHex: yo,
  formatHex8: sd,
  formatHsl: od,
  formatRgb: _o,
  toString: _o
});
function yo() {
  return this.rgb().formatHex();
}
function sd() {
  return this.rgb().formatHex8();
}
function od() {
  return su(this).formatHsl();
}
function _o() {
  return this.rgb().formatRgb();
}
function Ze(t) {
  var e, n;
  return t = (t + "").trim().toLowerCase(), (e = Qh.exec(t)) ? (n = e[1].length, e = parseInt(e[1], 16), n === 6 ? vo(e) : n === 3 ? new Ut(e >> 8 & 15 | e >> 4 & 240, e >> 4 & 15 | e & 240, (e & 15) << 4 | e & 15, 1) : n === 8 ? lr(e >> 24 & 255, e >> 16 & 255, e >> 8 & 255, (e & 255) / 255) : n === 4 ? lr(e >> 12 & 15 | e >> 8 & 240, e >> 8 & 15 | e >> 4 & 240, e >> 4 & 15 | e & 240, ((e & 15) << 4 | e & 15) / 255) : null) : (e = Zh.exec(t)) ? new Ut(e[1], e[2], e[3], 1) : (e = td.exec(t)) ? new Ut(e[1] * 255 / 100, e[2] * 255 / 100, e[3] * 255 / 100, 1) : (e = ed.exec(t)) ? lr(e[1], e[2], e[3], e[4]) : (e = nd.exec(t)) ? lr(e[1] * 255 / 100, e[2] * 255 / 100, e[3] * 255 / 100, e[4]) : (e = rd.exec(t)) ? Eo(e[1], e[2] / 100, e[3] / 100, 1) : (e = id.exec(t)) ? Eo(e[1], e[2] / 100, e[3] / 100, e[4]) : wo.hasOwnProperty(t) ? vo(wo[t]) : t === "transparent" ? new Ut(NaN, NaN, NaN, 0) : null;
}
function vo(t) {
  return new Ut(t >> 16 & 255, t >> 8 & 255, t & 255, 1);
}
function lr(t, e, n, r) {
  return r <= 0 && (t = e = n = NaN), new Ut(t, e, n, r);
}
function ld(t) {
  return t instanceof Yn || (t = Ze(t)), t ? (t = t.rgb(), new Ut(t.r, t.g, t.b, t.opacity)) : new Ut();
}
function zi(t, e, n, r) {
  return arguments.length === 1 ? ld(t) : new Ut(t, e, n, r ?? 1);
}
function Ut(t, e, n, r) {
  this.r = +t, this.g = +e, this.b = +n, this.opacity = +r;
}
ys(Ut, zi, iu(Yn, {
  brighter(t) {
    return t = t == null ? Or : Math.pow(Or, t), new Ut(this.r * t, this.g * t, this.b * t, this.opacity);
  },
  darker(t) {
    return t = t == null ? Vn : Math.pow(Vn, t), new Ut(this.r * t, this.g * t, this.b * t, this.opacity);
  },
  rgb() {
    return this;
  },
  clamp() {
    return new Ut(Qe(this.r), Qe(this.g), Qe(this.b), Ar(this.opacity));
  },
  displayable() {
    return -0.5 <= this.r && this.r < 255.5 && -0.5 <= this.g && this.g < 255.5 && -0.5 <= this.b && this.b < 255.5 && 0 <= this.opacity && this.opacity <= 1;
  },
  hex: bo,
  // Deprecated! Use color.formatHex.
  formatHex: bo,
  formatHex8: ud,
  formatRgb: xo,
  toString: xo
}));
function bo() {
  return `#${Xe(this.r)}${Xe(this.g)}${Xe(this.b)}`;
}
function ud() {
  return `#${Xe(this.r)}${Xe(this.g)}${Xe(this.b)}${Xe((isNaN(this.opacity) ? 1 : this.opacity) * 255)}`;
}
function xo() {
  const t = Ar(this.opacity);
  return `${t === 1 ? "rgb(" : "rgba("}${Qe(this.r)}, ${Qe(this.g)}, ${Qe(this.b)}${t === 1 ? ")" : `, ${t})`}`;
}
function Ar(t) {
  return isNaN(t) ? 1 : Math.max(0, Math.min(1, t));
}
function Qe(t) {
  return Math.max(0, Math.min(255, Math.round(t) || 0));
}
function Xe(t) {
  return t = Qe(t), (t < 16 ? "0" : "") + t.toString(16);
}
function Eo(t, e, n, r) {
  return r <= 0 ? t = e = n = NaN : n <= 0 || n >= 1 ? t = e = NaN : e <= 0 && (t = NaN), new ce(t, e, n, r);
}
function su(t) {
  if (t instanceof ce) return new ce(t.h, t.s, t.l, t.opacity);
  if (t instanceof Yn || (t = Ze(t)), !t) return new ce();
  if (t instanceof ce) return t;
  t = t.rgb();
  var e = t.r / 255, n = t.g / 255, r = t.b / 255, i = Math.min(e, n, r), s = Math.max(e, n, r), o = NaN, l = s - i, u = (s + i) / 2;
  return l ? (e === s ? o = (n - r) / l + (n < r) * 6 : n === s ? o = (r - e) / l + 2 : o = (e - n) / l + 4, l /= u < 0.5 ? s + i : 2 - s - i, o *= 60) : l = u > 0 && u < 1 ? 0 : o, new ce(o, l, u, t.opacity);
}
function cd(t, e, n, r) {
  return arguments.length === 1 ? su(t) : new ce(t, e, n, r ?? 1);
}
function ce(t, e, n, r) {
  this.h = +t, this.s = +e, this.l = +n, this.opacity = +r;
}
ys(ce, cd, iu(Yn, {
  brighter(t) {
    return t = t == null ? Or : Math.pow(Or, t), new ce(this.h, this.s, this.l * t, this.opacity);
  },
  darker(t) {
    return t = t == null ? Vn : Math.pow(Vn, t), new ce(this.h, this.s, this.l * t, this.opacity);
  },
  rgb() {
    var t = this.h % 360 + (this.h < 0) * 360, e = isNaN(t) || isNaN(this.s) ? 0 : this.s, n = this.l, r = n + (n < 0.5 ? n : 1 - n) * e, i = 2 * n - r;
    return new Ut(
      yi(t >= 240 ? t - 240 : t + 120, i, r),
      yi(t, i, r),
      yi(t < 120 ? t + 240 : t - 120, i, r),
      this.opacity
    );
  },
  clamp() {
    return new ce(So(this.h), ur(this.s), ur(this.l), Ar(this.opacity));
  },
  displayable() {
    return (0 <= this.s && this.s <= 1 || isNaN(this.s)) && 0 <= this.l && this.l <= 1 && 0 <= this.opacity && this.opacity <= 1;
  },
  formatHsl() {
    const t = Ar(this.opacity);
    return `${t === 1 ? "hsl(" : "hsla("}${So(this.h)}, ${ur(this.s) * 100}%, ${ur(this.l) * 100}%${t === 1 ? ")" : `, ${t})`}`;
  }
}));
function So(t) {
  return t = (t || 0) % 360, t < 0 ? t + 360 : t;
}
function ur(t) {
  return Math.max(0, Math.min(1, t || 0));
}
function yi(t, e, n) {
  return (t < 60 ? e + (n - e) * t / 60 : t < 180 ? n : t < 240 ? e + (n - e) * (240 - t) / 60 : e) * 255;
}
const _s = (t) => () => t;
function ad(t, e) {
  return function(n) {
    return t + n * e;
  };
}
function fd(t, e, n) {
  return t = Math.pow(t, n), e = Math.pow(e, n) - t, n = 1 / n, function(r) {
    return Math.pow(t + r * e, n);
  };
}
function hd(t) {
  return (t = +t) == 1 ? ou : function(e, n) {
    return n - e ? fd(e, n, t) : _s(isNaN(e) ? n : e);
  };
}
function ou(t, e) {
  var n = e - t;
  return n ? ad(t, n) : _s(isNaN(t) ? e : t);
}
const Fr = function t(e) {
  var n = hd(e);
  function r(i, s) {
    var o = n((i = zi(i)).r, (s = zi(s)).r), l = n(i.g, s.g), u = n(i.b, s.b), c = ou(i.opacity, s.opacity);
    return function(a) {
      return i.r = o(a), i.g = l(a), i.b = u(a), i.opacity = c(a), i + "";
    };
  }
  return r.gamma = t, r;
}(1);
function dd(t, e) {
  e || (e = []);
  var n = t ? Math.min(e.length, t.length) : 0, r = e.slice(), i;
  return function(s) {
    for (i = 0; i < n; ++i) r[i] = t[i] * (1 - s) + e[i] * s;
    return r;
  };
}
function pd(t) {
  return ArrayBuffer.isView(t) && !(t instanceof DataView);
}
function gd(t, e) {
  var n = e ? e.length : 0, r = t ? Math.min(n, t.length) : 0, i = new Array(r), s = new Array(n), o;
  for (o = 0; o < r; ++o) i[o] = vs(t[o], e[o]);
  for (; o < n; ++o) s[o] = e[o];
  return function(l) {
    for (o = 0; o < r; ++o) s[o] = i[o](l);
    return s;
  };
}
function md(t, e) {
  var n = /* @__PURE__ */ new Date();
  return t = +t, e = +e, function(r) {
    return n.setTime(t * (1 - r) + e * r), n;
  };
}
function we(t, e) {
  return t = +t, e = +e, function(n) {
    return t * (1 - n) + e * n;
  };
}
function wd(t, e) {
  var n = {}, r = {}, i;
  (t === null || typeof t != "object") && (t = {}), (e === null || typeof e != "object") && (e = {});
  for (i in e)
    i in t ? n[i] = vs(t[i], e[i]) : r[i] = e[i];
  return function(s) {
    for (i in n) r[i] = n[i](s);
    return r;
  };
}
var Di = /[-+]?(?:\d+\.?\d*|\.?\d+)(?:[eE][-+]?\d+)?/g, _i = new RegExp(Di.source, "g");
function yd(t) {
  return function() {
    return t;
  };
}
function _d(t) {
  return function(e) {
    return t(e) + "";
  };
}
function lu(t, e) {
  var n = Di.lastIndex = _i.lastIndex = 0, r, i, s, o = -1, l = [], u = [];
  for (t = t + "", e = e + ""; (r = Di.exec(t)) && (i = _i.exec(e)); )
    (s = i.index) > n && (s = e.slice(n, s), l[o] ? l[o] += s : l[++o] = s), (r = r[0]) === (i = i[0]) ? l[o] ? l[o] += i : l[++o] = i : (l[++o] = null, u.push({ i: o, x: we(r, i) })), n = _i.lastIndex;
  return n < e.length && (s = e.slice(n), l[o] ? l[o] += s : l[++o] = s), l.length < 2 ? u[0] ? _d(u[0].x) : yd(e) : (e = u.length, function(c) {
    for (var a = 0, f; a < e; ++a) l[(f = u[a]).i] = f.x(c);
    return l.join("");
  });
}
function vs(t, e) {
  var n = typeof e, r;
  return e == null || n === "boolean" ? _s(e) : (n === "number" ? we : n === "string" ? (r = Ze(e)) ? (e = r, Fr) : lu : e instanceof Ze ? Fr : e instanceof Date ? md : pd(e) ? dd : Array.isArray(e) ? gd : typeof e.valueOf != "function" && typeof e.toString != "function" || isNaN(e) ? wd : we)(t, e);
}
var ko = 180 / Math.PI, Vi = {
  translateX: 0,
  translateY: 0,
  rotate: 0,
  skewX: 0,
  scaleX: 1,
  scaleY: 1
};
function uu(t, e, n, r, i, s) {
  var o, l, u;
  return (o = Math.sqrt(t * t + e * e)) && (t /= o, e /= o), (u = t * n + e * r) && (n -= t * u, r -= e * u), (l = Math.sqrt(n * n + r * r)) && (n /= l, r /= l, u /= l), t * r < e * n && (t = -t, e = -e, u = -u, o = -o), {
    translateX: i,
    translateY: s,
    rotate: Math.atan2(e, t) * ko,
    skewX: Math.atan(u) * ko,
    scaleX: o,
    scaleY: l
  };
}
var cr;
function vd(t) {
  const e = new (typeof DOMMatrix == "function" ? DOMMatrix : WebKitCSSMatrix)(t + "");
  return e.isIdentity ? Vi : uu(e.a, e.b, e.c, e.d, e.e, e.f);
}
function bd(t) {
  return t == null || (cr || (cr = document.createElementNS("http://www.w3.org/2000/svg", "g")), cr.setAttribute("transform", t), !(t = cr.transform.baseVal.consolidate())) ? Vi : (t = t.matrix, uu(t.a, t.b, t.c, t.d, t.e, t.f));
}
function cu(t, e, n, r) {
  function i(c) {
    return c.length ? c.pop() + " " : "";
  }
  function s(c, a, f, h, p, w) {
    if (c !== f || a !== h) {
      var _ = p.push("translate(", null, e, null, n);
      w.push({ i: _ - 4, x: we(c, f) }, { i: _ - 2, x: we(a, h) });
    } else (f || h) && p.push("translate(" + f + e + h + n);
  }
  function o(c, a, f, h) {
    c !== a ? (c - a > 180 ? a += 360 : a - c > 180 && (c += 360), h.push({ i: f.push(i(f) + "rotate(", null, r) - 2, x: we(c, a) })) : a && f.push(i(f) + "rotate(" + a + r);
  }
  function l(c, a, f, h) {
    c !== a ? h.push({ i: f.push(i(f) + "skewX(", null, r) - 2, x: we(c, a) }) : a && f.push(i(f) + "skewX(" + a + r);
  }
  function u(c, a, f, h, p, w) {
    if (c !== f || a !== h) {
      var _ = p.push(i(p) + "scale(", null, ",", null, ")");
      w.push({ i: _ - 4, x: we(c, f) }, { i: _ - 2, x: we(a, h) });
    } else (f !== 1 || h !== 1) && p.push(i(p) + "scale(" + f + "," + h + ")");
  }
  return function(c, a) {
    var f = [], h = [];
    return c = t(c), a = t(a), s(c.translateX, c.translateY, a.translateX, a.translateY, f, h), o(c.rotate, a.rotate, f, h), l(c.skewX, a.skewX, f, h), u(c.scaleX, c.scaleY, a.scaleX, a.scaleY, f, h), c = a = null, function(p) {
      for (var w = -1, _ = h.length, v; ++w < _; ) f[(v = h[w]).i] = v.x(p);
      return f.join("");
    };
  };
}
var xd = cu(vd, "px, ", "px)", "deg)"), Ed = cu(bd, ", ", ")", ")"), Sd = 1e-12;
function Mo(t) {
  return ((t = Math.exp(t)) + 1 / t) / 2;
}
function kd(t) {
  return ((t = Math.exp(t)) - 1 / t) / 2;
}
function Md(t) {
  return ((t = Math.exp(2 * t)) - 1) / (t + 1);
}
const Nd = function t(e, n, r) {
  function i(s, o) {
    var l = s[0], u = s[1], c = s[2], a = o[0], f = o[1], h = o[2], p = a - l, w = f - u, _ = p * p + w * w, v, d;
    if (_ < Sd)
      d = Math.log(h / c) / e, v = function(z) {
        return [
          l + z * p,
          u + z * w,
          c * Math.exp(e * z * d)
        ];
      };
    else {
      var T = Math.sqrt(_), P = (h * h - c * c + r * _) / (2 * c * n * T), y = (h * h - c * c - r * _) / (2 * h * n * T), k = Math.log(Math.sqrt(P * P + 1) - P), L = Math.log(Math.sqrt(y * y + 1) - y);
      d = (L - k) / e, v = function(z) {
        var G = z * d, Q = Mo(k), X = c / (n * T) * (Q * Md(e * G + k) - kd(k));
        return [
          l + X * p,
          u + X * w,
          c * Q / Mo(e * G + k)
        ];
      };
    }
    return v.duration = d * 1e3 * e / Math.SQRT2, v;
  }
  return i.rho = function(s) {
    var o = Math.max(1e-3, +s), l = o * o, u = l * l;
    return t(o, l, u);
  }, i;
}(Math.SQRT2, 2, 4);
var yn = 0, Rn = 0, Sn = 0, au = 1e3, jr, Tn, Br = 0, tn = 0, ni = 0, Gn = typeof performance == "object" && performance.now ? performance : Date, fu = typeof window == "object" && window.requestAnimationFrame ? window.requestAnimationFrame.bind(window) : function(t) {
  setTimeout(t, 17);
};
function bs() {
  return tn || (fu(Rd), tn = Gn.now() + ni);
}
function Rd() {
  tn = 0;
}
function zr() {
  this._call = this._time = this._next = null;
}
zr.prototype = xs.prototype = {
  constructor: zr,
  restart: function(t, e, n) {
    if (typeof t != "function") throw new TypeError("callback is not a function");
    n = (n == null ? bs() : +n) + (e == null ? 0 : +e), !this._next && Tn !== this && (Tn ? Tn._next = this : jr = this, Tn = this), this._call = t, this._time = n, qi();
  },
  stop: function() {
    this._call && (this._call = null, this._time = 1 / 0, qi());
  }
};
function xs(t, e, n) {
  var r = new zr();
  return r.restart(t, e, n), r;
}
function Td() {
  bs(), ++yn;
  for (var t = jr, e; t; )
    (e = tn - t._time) >= 0 && t._call.call(void 0, e), t = t._next;
  --yn;
}
function No() {
  tn = (Br = Gn.now()) + ni, yn = Rn = 0;
  try {
    Td();
  } finally {
    yn = 0, Pd(), tn = 0;
  }
}
function Cd() {
  var t = Gn.now(), e = t - Br;
  e > au && (ni -= e, Br = t);
}
function Pd() {
  for (var t, e = jr, n, r = 1 / 0; e; )
    e._call ? (r > e._time && (r = e._time), t = e, e = e._next) : (n = e._next, e._next = null, e = t ? t._next = n : jr = n);
  Tn = t, qi(r);
}
function qi(t) {
  if (!yn) {
    Rn && (Rn = clearTimeout(Rn));
    var e = t - tn;
    e > 24 ? (t < 1 / 0 && (Rn = setTimeout(No, t - Gn.now() - ni)), Sn && (Sn = clearInterval(Sn))) : (Sn || (Br = Gn.now(), Sn = setInterval(Cd, au)), yn = 1, fu(No));
  }
}
function Ro(t, e, n) {
  var r = new zr();
  return e = e == null ? 0 : +e, r.restart((i) => {
    r.stop(), t(i + e);
  }, e, n), r;
}
var Ld = Kn("start", "end", "cancel", "interrupt"), Id = [], hu = 0, To = 1, Gi = 2, Sr = 3, Co = 4, Hi = 5, kr = 6;
function ri(t, e, n, r, i, s) {
  var o = t.__transition;
  if (!o) t.__transition = {};
  else if (n in o) return;
  $d(t, n, {
    name: e,
    index: r,
    // For context during callback.
    group: i,
    // For context during callback.
    on: Ld,
    tween: Id,
    time: s.time,
    delay: s.delay,
    duration: s.duration,
    ease: s.ease,
    timer: null,
    state: hu
  });
}
function Es(t, e) {
  var n = he(t, e);
  if (n.state > hu) throw new Error("too late; already scheduled");
  return n;
}
function be(t, e) {
  var n = he(t, e);
  if (n.state > Sr) throw new Error("too late; already running");
  return n;
}
function he(t, e) {
  var n = t.__transition;
  if (!n || !(n = n[e])) throw new Error("transition not found");
  return n;
}
function $d(t, e, n) {
  var r = t.__transition, i;
  r[e] = n, n.timer = xs(s, 0, n.time);
  function s(c) {
    n.state = To, n.timer.restart(o, n.delay, n.time), n.delay <= c && o(c - n.delay);
  }
  function o(c) {
    var a, f, h, p;
    if (n.state !== To) return u();
    for (a in r)
      if (p = r[a], p.name === n.name) {
        if (p.state === Sr) return Ro(o);
        p.state === Co ? (p.state = kr, p.timer.stop(), p.on.call("interrupt", t, t.__data__, p.index, p.group), delete r[a]) : +a < e && (p.state = kr, p.timer.stop(), p.on.call("cancel", t, t.__data__, p.index, p.group), delete r[a]);
      }
    if (Ro(function() {
      n.state === Sr && (n.state = Co, n.timer.restart(l, n.delay, n.time), l(c));
    }), n.state = Gi, n.on.call("start", t, t.__data__, n.index, n.group), n.state === Gi) {
      for (n.state = Sr, i = new Array(h = n.tween.length), a = 0, f = -1; a < h; ++a)
        (p = n.tween[a].value.call(t, t.__data__, n.index, n.group)) && (i[++f] = p);
      i.length = f + 1;
    }
  }
  function l(c) {
    for (var a = c < n.duration ? n.ease.call(null, c / n.duration) : (n.timer.restart(u), n.state = Hi, 1), f = -1, h = i.length; ++f < h; )
      i[f].call(t, a);
    n.state === Hi && (n.on.call("end", t, t.__data__, n.index, n.group), u());
  }
  function u() {
    n.state = kr, n.timer.stop(), delete r[e];
    for (var c in r) return;
    delete t.__transition;
  }
}
function Mr(t, e) {
  var n = t.__transition, r, i, s = !0, o;
  if (n) {
    e = e == null ? null : e + "";
    for (o in n) {
      if ((r = n[o]).name !== e) {
        s = !1;
        continue;
      }
      i = r.state > Gi && r.state < Hi, r.state = kr, r.timer.stop(), r.on.call(i ? "interrupt" : "cancel", t, t.__data__, r.index, r.group), delete n[o];
    }
    s && delete t.__transition;
  }
}
function Od(t) {
  return this.each(function() {
    Mr(this, t);
  });
}
function Ad(t, e) {
  var n, r;
  return function() {
    var i = be(this, t), s = i.tween;
    if (s !== n) {
      r = n = s;
      for (var o = 0, l = r.length; o < l; ++o)
        if (r[o].name === e) {
          r = r.slice(), r.splice(o, 1);
          break;
        }
    }
    i.tween = r;
  };
}
function Fd(t, e, n) {
  var r, i;
  if (typeof n != "function") throw new Error();
  return function() {
    var s = be(this, t), o = s.tween;
    if (o !== r) {
      i = (r = o).slice();
      for (var l = { name: e, value: n }, u = 0, c = i.length; u < c; ++u)
        if (i[u].name === e) {
          i[u] = l;
          break;
        }
      u === c && i.push(l);
    }
    s.tween = i;
  };
}
function jd(t, e) {
  var n = this._id;
  if (t += "", arguments.length < 2) {
    for (var r = he(this.node(), n).tween, i = 0, s = r.length, o; i < s; ++i)
      if ((o = r[i]).name === t)
        return o.value;
    return null;
  }
  return this.each((e == null ? Ad : Fd)(n, t, e));
}
function Ss(t, e, n) {
  var r = t._id;
  return t.each(function() {
    var i = be(this, r);
    (i.value || (i.value = {}))[e] = n.apply(this, arguments);
  }), function(i) {
    return he(i, r).value[e];
  };
}
function du(t, e) {
  var n;
  return (typeof e == "number" ? we : e instanceof Ze ? Fr : (n = Ze(e)) ? (e = n, Fr) : lu)(t, e);
}
function Bd(t) {
  return function() {
    this.removeAttribute(t);
  };
}
function zd(t) {
  return function() {
    this.removeAttributeNS(t.space, t.local);
  };
}
function Dd(t, e, n) {
  var r, i = n + "", s;
  return function() {
    var o = this.getAttribute(t);
    return o === i ? null : o === r ? s : s = e(r = o, n);
  };
}
function Vd(t, e, n) {
  var r, i = n + "", s;
  return function() {
    var o = this.getAttributeNS(t.space, t.local);
    return o === i ? null : o === r ? s : s = e(r = o, n);
  };
}
function qd(t, e, n) {
  var r, i, s;
  return function() {
    var o, l = n(this), u;
    return l == null ? void this.removeAttribute(t) : (o = this.getAttribute(t), u = l + "", o === u ? null : o === r && u === i ? s : (i = u, s = e(r = o, l)));
  };
}
function Gd(t, e, n) {
  var r, i, s;
  return function() {
    var o, l = n(this), u;
    return l == null ? void this.removeAttributeNS(t.space, t.local) : (o = this.getAttributeNS(t.space, t.local), u = l + "", o === u ? null : o === r && u === i ? s : (i = u, s = e(r = o, l)));
  };
}
function Hd(t, e) {
  var n = ei(t), r = n === "transform" ? Ed : du;
  return this.attrTween(t, typeof e == "function" ? (n.local ? Gd : qd)(n, r, Ss(this, "attr." + t, e)) : e == null ? (n.local ? zd : Bd)(n) : (n.local ? Vd : Dd)(n, r, e));
}
function Ud(t, e) {
  return function(n) {
    this.setAttribute(t, e.call(this, n));
  };
}
function Wd(t, e) {
  return function(n) {
    this.setAttributeNS(t.space, t.local, e.call(this, n));
  };
}
function Kd(t, e) {
  var n, r;
  function i() {
    var s = e.apply(this, arguments);
    return s !== r && (n = (r = s) && Wd(t, s)), n;
  }
  return i._value = e, i;
}
function Xd(t, e) {
  var n, r;
  function i() {
    var s = e.apply(this, arguments);
    return s !== r && (n = (r = s) && Ud(t, s)), n;
  }
  return i._value = e, i;
}
function Yd(t, e) {
  var n = "attr." + t;
  if (arguments.length < 2) return (n = this.tween(n)) && n._value;
  if (e == null) return this.tween(n, null);
  if (typeof e != "function") throw new Error();
  var r = ei(t);
  return this.tween(n, (r.local ? Kd : Xd)(r, e));
}
function Jd(t, e) {
  return function() {
    Es(this, t).delay = +e.apply(this, arguments);
  };
}
function Qd(t, e) {
  return e = +e, function() {
    Es(this, t).delay = e;
  };
}
function Zd(t) {
  var e = this._id;
  return arguments.length ? this.each((typeof t == "function" ? Jd : Qd)(e, t)) : he(this.node(), e).delay;
}
function tp(t, e) {
  return function() {
    be(this, t).duration = +e.apply(this, arguments);
  };
}
function ep(t, e) {
  return e = +e, function() {
    be(this, t).duration = e;
  };
}
function np(t) {
  var e = this._id;
  return arguments.length ? this.each((typeof t == "function" ? tp : ep)(e, t)) : he(this.node(), e).duration;
}
function rp(t, e) {
  if (typeof e != "function") throw new Error();
  return function() {
    be(this, t).ease = e;
  };
}
function ip(t) {
  var e = this._id;
  return arguments.length ? this.each(rp(e, t)) : he(this.node(), e).ease;
}
function sp(t, e) {
  return function() {
    var n = e.apply(this, arguments);
    if (typeof n != "function") throw new Error();
    be(this, t).ease = n;
  };
}
function op(t) {
  if (typeof t != "function") throw new Error();
  return this.each(sp(this._id, t));
}
function lp(t) {
  typeof t != "function" && (t = Hl(t));
  for (var e = this._groups, n = e.length, r = new Array(n), i = 0; i < n; ++i)
    for (var s = e[i], o = s.length, l = r[i] = [], u, c = 0; c < o; ++c)
      (u = s[c]) && t.call(u, u.__data__, c, s) && l.push(u);
  return new Ne(r, this._parents, this._name, this._id);
}
function up(t) {
  if (t._id !== this._id) throw new Error();
  for (var e = this._groups, n = t._groups, r = e.length, i = n.length, s = Math.min(r, i), o = new Array(r), l = 0; l < s; ++l)
    for (var u = e[l], c = n[l], a = u.length, f = o[l] = new Array(a), h, p = 0; p < a; ++p)
      (h = u[p] || c[p]) && (f[p] = h);
  for (; l < r; ++l)
    o[l] = e[l];
  return new Ne(o, this._parents, this._name, this._id);
}
function cp(t) {
  return (t + "").trim().split(/^|\s+/).every(function(e) {
    var n = e.indexOf(".");
    return n >= 0 && (e = e.slice(0, n)), !e || e === "start";
  });
}
function ap(t, e, n) {
  var r, i, s = cp(e) ? Es : be;
  return function() {
    var o = s(this, t), l = o.on;
    l !== r && (i = (r = l).copy()).on(e, n), o.on = i;
  };
}
function fp(t, e) {
  var n = this._id;
  return arguments.length < 2 ? he(this.node(), n).on.on(t) : this.each(ap(n, t, e));
}
function hp(t) {
  return function() {
    var e = this.parentNode;
    for (var n in this.__transition) if (+n !== t) return;
    e && e.removeChild(this);
  };
}
function dp() {
  return this.on("end.remove", hp(this._id));
}
function pp(t) {
  var e = this._name, n = this._id;
  typeof t != "function" && (t = ms(t));
  for (var r = this._groups, i = r.length, s = new Array(i), o = 0; o < i; ++o)
    for (var l = r[o], u = l.length, c = s[o] = new Array(u), a, f, h = 0; h < u; ++h)
      (a = l[h]) && (f = t.call(a, a.__data__, h, l)) && ("__data__" in a && (f.__data__ = a.__data__), c[h] = f, ri(c[h], e, n, h, c, he(a, n)));
  return new Ne(s, this._parents, e, n);
}
function gp(t) {
  var e = this._name, n = this._id;
  typeof t != "function" && (t = Gl(t));
  for (var r = this._groups, i = r.length, s = [], o = [], l = 0; l < i; ++l)
    for (var u = r[l], c = u.length, a, f = 0; f < c; ++f)
      if (a = u[f]) {
        for (var h = t.call(a, a.__data__, f, u), p, w = he(a, n), _ = 0, v = h.length; _ < v; ++_)
          (p = h[_]) && ri(p, e, n, _, h, w);
        s.push(h), o.push(a);
      }
  return new Ne(s, o, e, n);
}
var mp = Xn.prototype.constructor;
function wp() {
  return new mp(this._groups, this._parents);
}
function yp(t, e) {
  var n, r, i;
  return function() {
    var s = wn(this, t), o = (this.style.removeProperty(t), wn(this, t));
    return s === o ? null : s === n && o === r ? i : i = e(n = s, r = o);
  };
}
function pu(t) {
  return function() {
    this.style.removeProperty(t);
  };
}
function _p(t, e, n) {
  var r, i = n + "", s;
  return function() {
    var o = wn(this, t);
    return o === i ? null : o === r ? s : s = e(r = o, n);
  };
}
function vp(t, e, n) {
  var r, i, s;
  return function() {
    var o = wn(this, t), l = n(this), u = l + "";
    return l == null && (u = l = (this.style.removeProperty(t), wn(this, t))), o === u ? null : o === r && u === i ? s : (i = u, s = e(r = o, l));
  };
}
function bp(t, e) {
  var n, r, i, s = "style." + e, o = "end." + s, l;
  return function() {
    var u = be(this, t), c = u.on, a = u.value[s] == null ? l || (l = pu(e)) : void 0;
    (c !== n || i !== a) && (r = (n = c).copy()).on(o, i = a), u.on = r;
  };
}
function xp(t, e, n) {
  var r = (t += "") == "transform" ? xd : du;
  return e == null ? this.styleTween(t, yp(t, r)).on("end.style." + t, pu(t)) : typeof e == "function" ? this.styleTween(t, vp(t, r, Ss(this, "style." + t, e))).each(bp(this._id, t)) : this.styleTween(t, _p(t, r, e), n).on("end.style." + t, null);
}
function Ep(t, e, n) {
  return function(r) {
    this.style.setProperty(t, e.call(this, r), n);
  };
}
function Sp(t, e, n) {
  var r, i;
  function s() {
    var o = e.apply(this, arguments);
    return o !== i && (r = (i = o) && Ep(t, o, n)), r;
  }
  return s._value = e, s;
}
function kp(t, e, n) {
  var r = "style." + (t += "");
  if (arguments.length < 2) return (r = this.tween(r)) && r._value;
  if (e == null) return this.tween(r, null);
  if (typeof e != "function") throw new Error();
  return this.tween(r, Sp(t, e, n ?? ""));
}
function Mp(t) {
  return function() {
    this.textContent = t;
  };
}
function Np(t) {
  return function() {
    var e = t(this);
    this.textContent = e ?? "";
  };
}
function Rp(t) {
  return this.tween("text", typeof t == "function" ? Np(Ss(this, "text", t)) : Mp(t == null ? "" : t + ""));
}
function Tp(t) {
  return function(e) {
    this.textContent = t.call(this, e);
  };
}
function Cp(t) {
  var e, n;
  function r() {
    var i = t.apply(this, arguments);
    return i !== n && (e = (n = i) && Tp(i)), e;
  }
  return r._value = t, r;
}
function Pp(t) {
  var e = "text";
  if (arguments.length < 1) return (e = this.tween(e)) && e._value;
  if (t == null) return this.tween(e, null);
  if (typeof t != "function") throw new Error();
  return this.tween(e, Cp(t));
}
function Lp() {
  for (var t = this._name, e = this._id, n = gu(), r = this._groups, i = r.length, s = 0; s < i; ++s)
    for (var o = r[s], l = o.length, u, c = 0; c < l; ++c)
      if (u = o[c]) {
        var a = he(u, e);
        ri(u, t, n, c, o, {
          time: a.time + a.delay + a.duration,
          delay: 0,
          duration: a.duration,
          ease: a.ease
        });
      }
  return new Ne(r, this._parents, t, n);
}
function Ip() {
  var t, e, n = this, r = n._id, i = n.size();
  return new Promise(function(s, o) {
    var l = { value: o }, u = { value: function() {
      --i === 0 && s();
    } };
    n.each(function() {
      var c = be(this, r), a = c.on;
      a !== t && (e = (t = a).copy(), e._.cancel.push(l), e._.interrupt.push(l), e._.end.push(u)), c.on = e;
    }), i === 0 && s();
  });
}
var $p = 0;
function Ne(t, e, n, r) {
  this._groups = t, this._parents = e, this._name = n, this._id = r;
}
function gu() {
  return ++$p;
}
var Ee = Xn.prototype;
Ne.prototype = {
  constructor: Ne,
  select: pp,
  selectAll: gp,
  selectChild: Ee.selectChild,
  selectChildren: Ee.selectChildren,
  filter: lp,
  merge: up,
  selection: wp,
  transition: Lp,
  call: Ee.call,
  nodes: Ee.nodes,
  node: Ee.node,
  size: Ee.size,
  empty: Ee.empty,
  each: Ee.each,
  on: fp,
  attr: Hd,
  attrTween: Yd,
  style: xp,
  styleTween: kp,
  text: Rp,
  textTween: Pp,
  remove: dp,
  tween: jd,
  delay: Zd,
  duration: np,
  ease: ip,
  easeVarying: op,
  end: Ip,
  [Symbol.iterator]: Ee[Symbol.iterator]
};
const Op = (t) => +t;
function Ap(t) {
  return ((t *= 2) <= 1 ? t * t * t : (t -= 2) * t * t + 2) / 2;
}
var Fp = {
  time: null,
  // Set on use.
  delay: 0,
  duration: 250,
  ease: Ap
};
function jp(t, e) {
  for (var n; !(n = t.__transition) || !(n = n[e]); )
    if (!(t = t.parentNode))
      throw new Error(`transition ${e} not found`);
  return n;
}
function Bp(t) {
  var e, n;
  t instanceof Ne ? (e = t._id, t = t._name) : (e = gu(), (n = Fp).time = bs(), t = t == null ? null : t + "");
  for (var r = this._groups, i = r.length, s = 0; s < i; ++s)
    for (var o = r[s], l = o.length, u, c = 0; c < l; ++c)
      (u = o[c]) && ri(u, t, e, c, o, n || jp(u, e));
  return new Ne(r, this._parents, t, e);
}
Xn.prototype.interrupt = Od;
Xn.prototype.transition = Bp;
const Ui = Math.PI, Wi = 2 * Ui, Ue = 1e-6, zp = Wi - Ue;
function mu(t) {
  this._ += t[0];
  for (let e = 1, n = t.length; e < n; ++e)
    this._ += arguments[e] + t[e];
}
function Dp(t) {
  let e = Math.floor(t);
  if (!(e >= 0)) throw new Error(`invalid digits: ${t}`);
  if (e > 15) return mu;
  const n = 10 ** e;
  return function(r) {
    this._ += r[0];
    for (let i = 1, s = r.length; i < s; ++i)
      this._ += Math.round(arguments[i] * n) / n + r[i];
  };
}
class Vp {
  constructor(e) {
    this._x0 = this._y0 = // start of current subpath
    this._x1 = this._y1 = null, this._ = "", this._append = e == null ? mu : Dp(e);
  }
  moveTo(e, n) {
    this._append`M${this._x0 = this._x1 = +e},${this._y0 = this._y1 = +n}`;
  }
  closePath() {
    this._x1 !== null && (this._x1 = this._x0, this._y1 = this._y0, this._append`Z`);
  }
  lineTo(e, n) {
    this._append`L${this._x1 = +e},${this._y1 = +n}`;
  }
  quadraticCurveTo(e, n, r, i) {
    this._append`Q${+e},${+n},${this._x1 = +r},${this._y1 = +i}`;
  }
  bezierCurveTo(e, n, r, i, s, o) {
    this._append`C${+e},${+n},${+r},${+i},${this._x1 = +s},${this._y1 = +o}`;
  }
  arcTo(e, n, r, i, s) {
    if (e = +e, n = +n, r = +r, i = +i, s = +s, s < 0) throw new Error(`negative radius: ${s}`);
    let o = this._x1, l = this._y1, u = r - e, c = i - n, a = o - e, f = l - n, h = a * a + f * f;
    if (this._x1 === null)
      this._append`M${this._x1 = e},${this._y1 = n}`;
    else if (h > Ue) if (!(Math.abs(f * u - c * a) > Ue) || !s)
      this._append`L${this._x1 = e},${this._y1 = n}`;
    else {
      let p = r - o, w = i - l, _ = u * u + c * c, v = p * p + w * w, d = Math.sqrt(_), T = Math.sqrt(h), P = s * Math.tan((Ui - Math.acos((_ + h - v) / (2 * d * T))) / 2), y = P / T, k = P / d;
      Math.abs(y - 1) > Ue && this._append`L${e + y * a},${n + y * f}`, this._append`A${s},${s},0,0,${+(f * p > a * w)},${this._x1 = e + k * u},${this._y1 = n + k * c}`;
    }
  }
  arc(e, n, r, i, s, o) {
    if (e = +e, n = +n, r = +r, o = !!o, r < 0) throw new Error(`negative radius: ${r}`);
    let l = r * Math.cos(i), u = r * Math.sin(i), c = e + l, a = n + u, f = 1 ^ o, h = o ? i - s : s - i;
    this._x1 === null ? this._append`M${c},${a}` : (Math.abs(this._x1 - c) > Ue || Math.abs(this._y1 - a) > Ue) && this._append`L${c},${a}`, r && (h < 0 && (h = h % Wi + Wi), h > zp ? this._append`A${r},${r},0,1,${f},${e - l},${n - u}A${r},${r},0,1,${f},${this._x1 = c},${this._y1 = a}` : h > Ue && this._append`A${r},${r},0,${+(h >= Ui)},${f},${this._x1 = e + r * Math.cos(s)},${this._y1 = n + r * Math.sin(s)}`);
  }
  rect(e, n, r, i) {
    this._append`M${this._x0 = this._x1 = +e},${this._y0 = this._y1 = +n}h${r = +r}v${+i}h${-r}Z`;
  }
  toString() {
    return this._;
  }
}
function qp(t) {
  const e = +this._x.call(null, t), n = +this._y.call(null, t);
  return wu(this.cover(e, n), e, n, t);
}
function wu(t, e, n, r) {
  if (isNaN(e) || isNaN(n)) return t;
  var i, s = t._root, o = { data: r }, l = t._x0, u = t._y0, c = t._x1, a = t._y1, f, h, p, w, _, v, d, T;
  if (!s) return t._root = o, t;
  for (; s.length; )
    if ((_ = e >= (f = (l + c) / 2)) ? l = f : c = f, (v = n >= (h = (u + a) / 2)) ? u = h : a = h, i = s, !(s = s[d = v << 1 | _])) return i[d] = o, t;
  if (p = +t._x.call(null, s.data), w = +t._y.call(null, s.data), e === p && n === w) return o.next = s, i ? i[d] = o : t._root = o, t;
  do
    i = i ? i[d] = new Array(4) : t._root = new Array(4), (_ = e >= (f = (l + c) / 2)) ? l = f : c = f, (v = n >= (h = (u + a) / 2)) ? u = h : a = h;
  while ((d = v << 1 | _) === (T = (w >= h) << 1 | p >= f));
  return i[T] = s, i[d] = o, t;
}
function Gp(t) {
  var e, n, r = t.length, i, s, o = new Array(r), l = new Array(r), u = 1 / 0, c = 1 / 0, a = -1 / 0, f = -1 / 0;
  for (n = 0; n < r; ++n)
    isNaN(i = +this._x.call(null, e = t[n])) || isNaN(s = +this._y.call(null, e)) || (o[n] = i, l[n] = s, i < u && (u = i), i > a && (a = i), s < c && (c = s), s > f && (f = s));
  if (u > a || c > f) return this;
  for (this.cover(u, c).cover(a, f), n = 0; n < r; ++n)
    wu(this, o[n], l[n], t[n]);
  return this;
}
function Hp(t, e) {
  if (isNaN(t = +t) || isNaN(e = +e)) return this;
  var n = this._x0, r = this._y0, i = this._x1, s = this._y1;
  if (isNaN(n))
    i = (n = Math.floor(t)) + 1, s = (r = Math.floor(e)) + 1;
  else {
    for (var o = i - n || 1, l = this._root, u, c; n > t || t >= i || r > e || e >= s; )
      switch (c = (e < r) << 1 | t < n, u = new Array(4), u[c] = l, l = u, o *= 2, c) {
        case 0:
          i = n + o, s = r + o;
          break;
        case 1:
          n = i - o, s = r + o;
          break;
        case 2:
          i = n + o, r = s - o;
          break;
        case 3:
          n = i - o, r = s - o;
          break;
      }
    this._root && this._root.length && (this._root = l);
  }
  return this._x0 = n, this._y0 = r, this._x1 = i, this._y1 = s, this;
}
function Up() {
  var t = [];
  return this.visit(function(e) {
    if (!e.length) do
      t.push(e.data);
    while (e = e.next);
  }), t;
}
function Wp(t) {
  return arguments.length ? this.cover(+t[0][0], +t[0][1]).cover(+t[1][0], +t[1][1]) : isNaN(this._x0) ? void 0 : [[this._x0, this._y0], [this._x1, this._y1]];
}
function jt(t, e, n, r, i) {
  this.node = t, this.x0 = e, this.y0 = n, this.x1 = r, this.y1 = i;
}
function Kp(t, e, n) {
  var r, i = this._x0, s = this._y0, o, l, u, c, a = this._x1, f = this._y1, h = [], p = this._root, w, _;
  for (p && h.push(new jt(p, i, s, a, f)), n == null ? n = 1 / 0 : (i = t - n, s = e - n, a = t + n, f = e + n, n *= n); w = h.pop(); )
    if (!(!(p = w.node) || (o = w.x0) > a || (l = w.y0) > f || (u = w.x1) < i || (c = w.y1) < s))
      if (p.length) {
        var v = (o + u) / 2, d = (l + c) / 2;
        h.push(
          new jt(p[3], v, d, u, c),
          new jt(p[2], o, d, v, c),
          new jt(p[1], v, l, u, d),
          new jt(p[0], o, l, v, d)
        ), (_ = (e >= d) << 1 | t >= v) && (w = h[h.length - 1], h[h.length - 1] = h[h.length - 1 - _], h[h.length - 1 - _] = w);
      } else {
        var T = t - +this._x.call(null, p.data), P = e - +this._y.call(null, p.data), y = T * T + P * P;
        if (y < n) {
          var k = Math.sqrt(n = y);
          i = t - k, s = e - k, a = t + k, f = e + k, r = p.data;
        }
      }
  return r;
}
function Xp(t) {
  if (isNaN(a = +this._x.call(null, t)) || isNaN(f = +this._y.call(null, t))) return this;
  var e, n = this._root, r, i, s, o = this._x0, l = this._y0, u = this._x1, c = this._y1, a, f, h, p, w, _, v, d;
  if (!n) return this;
  if (n.length) for (; ; ) {
    if ((w = a >= (h = (o + u) / 2)) ? o = h : u = h, (_ = f >= (p = (l + c) / 2)) ? l = p : c = p, e = n, !(n = n[v = _ << 1 | w])) return this;
    if (!n.length) break;
    (e[v + 1 & 3] || e[v + 2 & 3] || e[v + 3 & 3]) && (r = e, d = v);
  }
  for (; n.data !== t; ) if (i = n, !(n = n.next)) return this;
  return (s = n.next) && delete n.next, i ? (s ? i.next = s : delete i.next, this) : e ? (s ? e[v] = s : delete e[v], (n = e[0] || e[1] || e[2] || e[3]) && n === (e[3] || e[2] || e[1] || e[0]) && !n.length && (r ? r[d] = n : this._root = n), this) : (this._root = s, this);
}
function Yp(t) {
  for (var e = 0, n = t.length; e < n; ++e) this.remove(t[e]);
  return this;
}
function Jp() {
  return this._root;
}
function Qp() {
  var t = 0;
  return this.visit(function(e) {
    if (!e.length) do
      ++t;
    while (e = e.next);
  }), t;
}
function Zp(t) {
  var e = [], n, r = this._root, i, s, o, l, u;
  for (r && e.push(new jt(r, this._x0, this._y0, this._x1, this._y1)); n = e.pop(); )
    if (!t(r = n.node, s = n.x0, o = n.y0, l = n.x1, u = n.y1) && r.length) {
      var c = (s + l) / 2, a = (o + u) / 2;
      (i = r[3]) && e.push(new jt(i, c, a, l, u)), (i = r[2]) && e.push(new jt(i, s, a, c, u)), (i = r[1]) && e.push(new jt(i, c, o, l, a)), (i = r[0]) && e.push(new jt(i, s, o, c, a));
    }
  return this;
}
function tg(t) {
  var e = [], n = [], r;
  for (this._root && e.push(new jt(this._root, this._x0, this._y0, this._x1, this._y1)); r = e.pop(); ) {
    var i = r.node;
    if (i.length) {
      var s, o = r.x0, l = r.y0, u = r.x1, c = r.y1, a = (o + u) / 2, f = (l + c) / 2;
      (s = i[0]) && e.push(new jt(s, o, l, a, f)), (s = i[1]) && e.push(new jt(s, a, l, u, f)), (s = i[2]) && e.push(new jt(s, o, f, a, c)), (s = i[3]) && e.push(new jt(s, a, f, u, c));
    }
    n.push(r);
  }
  for (; r = n.pop(); )
    t(r.node, r.x0, r.y0, r.x1, r.y1);
  return this;
}
function eg(t) {
  return t[0];
}
function ng(t) {
  return arguments.length ? (this._x = t, this) : this._x;
}
function rg(t) {
  return t[1];
}
function ig(t) {
  return arguments.length ? (this._y = t, this) : this._y;
}
function ks(t, e, n) {
  var r = new Ms(e ?? eg, n ?? rg, NaN, NaN, NaN, NaN);
  return t == null ? r : r.addAll(t);
}
function Ms(t, e, n, r, i, s) {
  this._x = t, this._y = e, this._x0 = n, this._y0 = r, this._x1 = i, this._y1 = s, this._root = void 0;
}
function Po(t) {
  for (var e = { data: t.data }, n = e; t = t.next; ) n = n.next = { data: t.data };
  return e;
}
var zt = ks.prototype = Ms.prototype;
zt.copy = function() {
  var t = new Ms(this._x, this._y, this._x0, this._y0, this._x1, this._y1), e = this._root, n, r;
  if (!e) return t;
  if (!e.length) return t._root = Po(e), t;
  for (n = [{ source: e, target: t._root = new Array(4) }]; e = n.pop(); )
    for (var i = 0; i < 4; ++i)
      (r = e.source[i]) && (r.length ? n.push({ source: r, target: e.target[i] = new Array(4) }) : e.target[i] = Po(r));
  return t;
};
zt.add = qp;
zt.addAll = Gp;
zt.cover = Hp;
zt.data = Up;
zt.extent = Wp;
zt.find = Kp;
zt.remove = Xp;
zt.removeAll = Yp;
zt.root = Jp;
zt.size = Qp;
zt.visit = Zp;
zt.visitAfter = tg;
zt.x = ng;
zt.y = ig;
function Bt(t) {
  return function() {
    return t;
  };
}
function Fe(t) {
  return (t() - 0.5) * 1e-6;
}
function sg(t) {
  return t.x + t.vx;
}
function og(t) {
  return t.y + t.vy;
}
function lg(t) {
  var e, n, r, i = 1, s = 1;
  typeof t != "function" && (t = Bt(t == null ? 1 : +t));
  function o() {
    for (var c, a = e.length, f, h, p, w, _, v, d = 0; d < s; ++d)
      for (f = ks(e, sg, og).visitAfter(l), c = 0; c < a; ++c)
        h = e[c], _ = n[h.index], v = _ * _, p = h.x + h.vx, w = h.y + h.vy, f.visit(T);
    function T(P, y, k, L, z) {
      var G = P.data, Q = P.r, X = _ + Q;
      if (G) {
        if (G.index > h.index) {
          var et = p - G.x - G.vx, ot = w - G.y - G.vy, U = et * et + ot * ot;
          U < X * X && (et === 0 && (et = Fe(r), U += et * et), ot === 0 && (ot = Fe(r), U += ot * ot), U = (X - (U = Math.sqrt(U))) / U * i, h.vx += (et *= U) * (X = (Q *= Q) / (v + Q)), h.vy += (ot *= U) * X, G.vx -= et * (X = 1 - X), G.vy -= ot * X);
        }
        return;
      }
      return y > p + X || L < p - X || k > w + X || z < w - X;
    }
  }
  function l(c) {
    if (c.data) return c.r = n[c.data.index];
    for (var a = c.r = 0; a < 4; ++a)
      c[a] && c[a].r > c.r && (c.r = c[a].r);
  }
  function u() {
    if (e) {
      var c, a = e.length, f;
      for (n = new Array(a), c = 0; c < a; ++c) f = e[c], n[f.index] = +t(f, c, e);
    }
  }
  return o.initialize = function(c, a) {
    e = c, r = a, u();
  }, o.iterations = function(c) {
    return arguments.length ? (s = +c, o) : s;
  }, o.strength = function(c) {
    return arguments.length ? (i = +c, o) : i;
  }, o.radius = function(c) {
    return arguments.length ? (t = typeof c == "function" ? c : Bt(+c), u(), o) : t;
  }, o;
}
function ug(t) {
  return t.index;
}
function Lo(t, e) {
  var n = t.get(e);
  if (!n) throw new Error("node not found: " + e);
  return n;
}
function cg(t) {
  var e = ug, n = f, r, i = Bt(30), s, o, l, u, c, a = 1;
  t == null && (t = []);
  function f(v) {
    return 1 / Math.min(l[v.source.index], l[v.target.index]);
  }
  function h(v) {
    for (var d = 0, T = t.length; d < a; ++d)
      for (var P = 0, y, k, L, z, G, Q, X; P < T; ++P)
        y = t[P], k = y.source, L = y.target, z = L.x + L.vx - k.x - k.vx || Fe(c), G = L.y + L.vy - k.y - k.vy || Fe(c), Q = Math.sqrt(z * z + G * G), Q = (Q - s[P]) / Q * v * r[P], z *= Q, G *= Q, L.vx -= z * (X = u[P]), L.vy -= G * X, k.vx += z * (X = 1 - X), k.vy += G * X;
  }
  function p() {
    if (o) {
      var v, d = o.length, T = t.length, P = new Map(o.map((k, L) => [e(k, L, o), k])), y;
      for (v = 0, l = new Array(d); v < T; ++v)
        y = t[v], y.index = v, typeof y.source != "object" && (y.source = Lo(P, y.source)), typeof y.target != "object" && (y.target = Lo(P, y.target)), l[y.source.index] = (l[y.source.index] || 0) + 1, l[y.target.index] = (l[y.target.index] || 0) + 1;
      for (v = 0, u = new Array(T); v < T; ++v)
        y = t[v], u[v] = l[y.source.index] / (l[y.source.index] + l[y.target.index]);
      r = new Array(T), w(), s = new Array(T), _();
    }
  }
  function w() {
    if (o)
      for (var v = 0, d = t.length; v < d; ++v)
        r[v] = +n(t[v], v, t);
  }
  function _() {
    if (o)
      for (var v = 0, d = t.length; v < d; ++v)
        s[v] = +i(t[v], v, t);
  }
  return h.initialize = function(v, d) {
    o = v, c = d, p();
  }, h.links = function(v) {
    return arguments.length ? (t = v, p(), h) : t;
  }, h.id = function(v) {
    return arguments.length ? (e = v, h) : e;
  }, h.iterations = function(v) {
    return arguments.length ? (a = +v, h) : a;
  }, h.strength = function(v) {
    return arguments.length ? (n = typeof v == "function" ? v : Bt(+v), w(), h) : n;
  }, h.distance = function(v) {
    return arguments.length ? (i = typeof v == "function" ? v : Bt(+v), _(), h) : i;
  }, h;
}
const ag = 1664525, fg = 1013904223, Io = 4294967296;
function hg() {
  let t = 1;
  return () => (t = (ag * t + fg) % Io) / Io;
}
function dg(t) {
  return t.x;
}
function pg(t) {
  return t.y;
}
var gg = 10, mg = Math.PI * (3 - Math.sqrt(5));
function wg(t) {
  var e, n = 1, r = 1e-3, i = 1 - Math.pow(r, 1 / 300), s = 0, o = 0.6, l = /* @__PURE__ */ new Map(), u = xs(f), c = Kn("tick", "end"), a = hg();
  t == null && (t = []);
  function f() {
    h(), c.call("tick", e), n < r && (u.stop(), c.call("end", e));
  }
  function h(_) {
    var v, d = t.length, T;
    _ === void 0 && (_ = 1);
    for (var P = 0; P < _; ++P)
      for (n += (s - n) * i, l.forEach(function(y) {
        y(n);
      }), v = 0; v < d; ++v)
        T = t[v], T.fx == null ? T.x += T.vx *= o : (T.x = T.fx, T.vx = 0), T.fy == null ? T.y += T.vy *= o : (T.y = T.fy, T.vy = 0);
    return e;
  }
  function p() {
    for (var _ = 0, v = t.length, d; _ < v; ++_) {
      if (d = t[_], d.index = _, d.fx != null && (d.x = d.fx), d.fy != null && (d.y = d.fy), isNaN(d.x) || isNaN(d.y)) {
        var T = gg * Math.sqrt(0.5 + _), P = _ * mg;
        d.x = T * Math.cos(P), d.y = T * Math.sin(P);
      }
      (isNaN(d.vx) || isNaN(d.vy)) && (d.vx = d.vy = 0);
    }
  }
  function w(_) {
    return _.initialize && _.initialize(t, a), _;
  }
  return p(), e = {
    tick: h,
    restart: function() {
      return u.restart(f), e;
    },
    stop: function() {
      return u.stop(), e;
    },
    nodes: function(_) {
      return arguments.length ? (t = _, p(), l.forEach(w), e) : t;
    },
    alpha: function(_) {
      return arguments.length ? (n = +_, e) : n;
    },
    alphaMin: function(_) {
      return arguments.length ? (r = +_, e) : r;
    },
    alphaDecay: function(_) {
      return arguments.length ? (i = +_, e) : +i;
    },
    alphaTarget: function(_) {
      return arguments.length ? (s = +_, e) : s;
    },
    velocityDecay: function(_) {
      return arguments.length ? (o = 1 - _, e) : 1 - o;
    },
    randomSource: function(_) {
      return arguments.length ? (a = _, l.forEach(w), e) : a;
    },
    force: function(_, v) {
      return arguments.length > 1 ? (v == null ? l.delete(_) : l.set(_, w(v)), e) : l.get(_);
    },
    find: function(_, v, d) {
      var T = 0, P = t.length, y, k, L, z, G;
      for (d == null ? d = 1 / 0 : d *= d, T = 0; T < P; ++T)
        z = t[T], y = _ - z.x, k = v - z.y, L = y * y + k * k, L < d && (G = z, d = L);
      return G;
    },
    on: function(_, v) {
      return arguments.length > 1 ? (c.on(_, v), e) : c.on(_);
    }
  };
}
function yg() {
  var t, e, n, r, i = Bt(-30), s, o = 1, l = 1 / 0, u = 0.81;
  function c(p) {
    var w, _ = t.length, v = ks(t, dg, pg).visitAfter(f);
    for (r = p, w = 0; w < _; ++w) e = t[w], v.visit(h);
  }
  function a() {
    if (t) {
      var p, w = t.length, _;
      for (s = new Array(w), p = 0; p < w; ++p) _ = t[p], s[_.index] = +i(_, p, t);
    }
  }
  function f(p) {
    var w = 0, _, v, d = 0, T, P, y;
    if (p.length) {
      for (T = P = y = 0; y < 4; ++y)
        (_ = p[y]) && (v = Math.abs(_.value)) && (w += _.value, d += v, T += v * _.x, P += v * _.y);
      p.x = T / d, p.y = P / d;
    } else {
      _ = p, _.x = _.data.x, _.y = _.data.y;
      do
        w += s[_.data.index];
      while (_ = _.next);
    }
    p.value = w;
  }
  function h(p, w, _, v) {
    if (!p.value) return !0;
    var d = p.x - e.x, T = p.y - e.y, P = v - w, y = d * d + T * T;
    if (P * P / u < y)
      return y < l && (d === 0 && (d = Fe(n), y += d * d), T === 0 && (T = Fe(n), y += T * T), y < o && (y = Math.sqrt(o * y)), e.vx += d * p.value * r / y, e.vy += T * p.value * r / y), !0;
    if (p.length || y >= l) return;
    (p.data !== e || p.next) && (d === 0 && (d = Fe(n), y += d * d), T === 0 && (T = Fe(n), y += T * T), y < o && (y = Math.sqrt(o * y)));
    do
      p.data !== e && (P = s[p.data.index] * r / y, e.vx += d * P, e.vy += T * P);
    while (p = p.next);
  }
  return c.initialize = function(p, w) {
    t = p, n = w, a();
  }, c.strength = function(p) {
    return arguments.length ? (i = typeof p == "function" ? p : Bt(+p), a(), c) : i;
  }, c.distanceMin = function(p) {
    return arguments.length ? (o = p * p, c) : Math.sqrt(o);
  }, c.distanceMax = function(p) {
    return arguments.length ? (l = p * p, c) : Math.sqrt(l);
  }, c.theta = function(p) {
    return arguments.length ? (u = p * p, c) : Math.sqrt(u);
  }, c;
}
function _g(t) {
  var e = Bt(0.1), n, r, i;
  typeof t != "function" && (t = Bt(t == null ? 0 : +t));
  function s(l) {
    for (var u = 0, c = n.length, a; u < c; ++u)
      a = n[u], a.vx += (i[u] - a.x) * r[u] * l;
  }
  function o() {
    if (n) {
      var l, u = n.length;
      for (r = new Array(u), i = new Array(u), l = 0; l < u; ++l)
        r[l] = isNaN(i[l] = +t(n[l], l, n)) ? 0 : +e(n[l], l, n);
    }
  }
  return s.initialize = function(l) {
    n = l, o();
  }, s.strength = function(l) {
    return arguments.length ? (e = typeof l == "function" ? l : Bt(+l), o(), s) : e;
  }, s.x = function(l) {
    return arguments.length ? (t = typeof l == "function" ? l : Bt(+l), o(), s) : t;
  }, s;
}
function vg(t) {
  var e = Bt(0.1), n, r, i;
  typeof t != "function" && (t = Bt(t == null ? 0 : +t));
  function s(l) {
    for (var u = 0, c = n.length, a; u < c; ++u)
      a = n[u], a.vy += (i[u] - a.y) * r[u] * l;
  }
  function o() {
    if (n) {
      var l, u = n.length;
      for (r = new Array(u), i = new Array(u), l = 0; l < u; ++l)
        r[l] = isNaN(i[l] = +t(n[l], l, n)) ? 0 : +e(n[l], l, n);
    }
  }
  return s.initialize = function(l) {
    n = l, o();
  }, s.strength = function(l) {
    return arguments.length ? (e = typeof l == "function" ? l : Bt(+l), o(), s) : e;
  }, s.y = function(l) {
    return arguments.length ? (t = typeof l == "function" ? l : Bt(+l), o(), s) : t;
  }, s;
}
function Ht(t) {
  return function() {
    return t;
  };
}
const $o = Math.abs, Pt = Math.atan2, He = Math.cos, bg = Math.max, vi = Math.min, ge = Math.sin, ln = Math.sqrt, qt = 1e-12, Hn = Math.PI, Dr = Hn / 2, xg = 2 * Hn;
function Eg(t) {
  return t > 1 ? 0 : t < -1 ? Hn : Math.acos(t);
}
function Oo(t) {
  return t >= 1 ? Dr : t <= -1 ? -Dr : Math.asin(t);
}
function yu(t) {
  let e = 3;
  return t.digits = function(n) {
    if (!arguments.length) return e;
    if (n == null)
      e = null;
    else {
      const r = Math.floor(n);
      if (!(r >= 0)) throw new RangeError(`invalid digits: ${n}`);
      e = r;
    }
    return t;
  }, () => new Vp(e);
}
function Sg(t) {
  return t.innerRadius;
}
function kg(t) {
  return t.outerRadius;
}
function Mg(t) {
  return t.startAngle;
}
function Ng(t) {
  return t.endAngle;
}
function Rg(t) {
  return t && t.padAngle;
}
function Tg(t, e, n, r, i, s, o, l) {
  var u = n - t, c = r - e, a = o - i, f = l - s, h = f * u - a * c;
  if (!(h * h < qt))
    return h = (a * (e - s) - f * (t - i)) / h, [t + h * u, e + h * c];
}
function ar(t, e, n, r, i, s, o) {
  var l = t - n, u = e - r, c = (o ? s : -s) / ln(l * l + u * u), a = c * u, f = -c * l, h = t + a, p = e + f, w = n + a, _ = r + f, v = (h + w) / 2, d = (p + _) / 2, T = w - h, P = _ - p, y = T * T + P * P, k = i - s, L = h * _ - w * p, z = (P < 0 ? -1 : 1) * ln(bg(0, k * k * y - L * L)), G = (L * P - T * z) / y, Q = (-L * T - P * z) / y, X = (L * P + T * z) / y, et = (-L * T + P * z) / y, ot = G - v, U = Q - d, x = X - v, D = et - d;
  return ot * ot + U * U > x * x + D * D && (G = X, Q = et), {
    cx: G,
    cy: Q,
    x01: -a,
    y01: -f,
    x11: G * (i / k - 1),
    y11: Q * (i / k - 1)
  };
}
function Cg() {
  var t = Sg, e = kg, n = Ht(0), r = null, i = Mg, s = Ng, o = Rg, l = null, u = yu(c);
  function c() {
    var a, f, h = +t.apply(this, arguments), p = +e.apply(this, arguments), w = i.apply(this, arguments) - Dr, _ = s.apply(this, arguments) - Dr, v = $o(_ - w), d = _ > w;
    if (l || (l = a = u()), p < h && (f = p, p = h, h = f), !(p > qt)) l.moveTo(0, 0);
    else if (v > xg - qt)
      l.moveTo(p * He(w), p * ge(w)), l.arc(0, 0, p, w, _, !d), h > qt && (l.moveTo(h * He(_), h * ge(_)), l.arc(0, 0, h, _, w, d));
    else {
      var T = w, P = _, y = w, k = _, L = v, z = v, G = o.apply(this, arguments) / 2, Q = G > qt && (r ? +r.apply(this, arguments) : ln(h * h + p * p)), X = vi($o(p - h) / 2, +n.apply(this, arguments)), et = X, ot = X, U, x;
      if (Q > qt) {
        var D = Oo(Q / h * ge(G)), N = Oo(Q / p * ge(G));
        (L -= D * 2) > qt ? (D *= d ? 1 : -1, y += D, k -= D) : (L = 0, y = k = (w + _) / 2), (z -= N * 2) > qt ? (N *= d ? 1 : -1, T += N, P -= N) : (z = 0, T = P = (w + _) / 2);
      }
      var j = p * He(T), B = p * ge(T), Y = h * He(k), J = h * ge(k);
      if (X > qt) {
        var it = p * He(P), lt = p * ge(P), kt = h * He(y), pt = h * ge(y), gt;
        if (v < Hn)
          if (gt = Tg(j, B, kt, pt, it, lt, Y, J)) {
            var mt = j - gt[0], xt = B - gt[1], Mt = it - gt[0], Dt = lt - gt[1], Zt = 1 / ge(Eg((mt * Mt + xt * Dt) / (ln(mt * mt + xt * xt) * ln(Mt * Mt + Dt * Dt))) / 2), de = ln(gt[0] * gt[0] + gt[1] * gt[1]);
            et = vi(X, (h - de) / (Zt - 1)), ot = vi(X, (p - de) / (Zt + 1));
          } else
            et = ot = 0;
      }
      z > qt ? ot > qt ? (U = ar(kt, pt, j, B, p, ot, d), x = ar(it, lt, Y, J, p, ot, d), l.moveTo(U.cx + U.x01, U.cy + U.y01), ot < X ? l.arc(U.cx, U.cy, ot, Pt(U.y01, U.x01), Pt(x.y01, x.x01), !d) : (l.arc(U.cx, U.cy, ot, Pt(U.y01, U.x01), Pt(U.y11, U.x11), !d), l.arc(0, 0, p, Pt(U.cy + U.y11, U.cx + U.x11), Pt(x.cy + x.y11, x.cx + x.x11), !d), l.arc(x.cx, x.cy, ot, Pt(x.y11, x.x11), Pt(x.y01, x.x01), !d))) : (l.moveTo(j, B), l.arc(0, 0, p, T, P, !d)) : l.moveTo(j, B), !(h > qt) || !(L > qt) ? l.lineTo(Y, J) : et > qt ? (U = ar(Y, J, it, lt, h, -et, d), x = ar(j, B, kt, pt, h, -et, d), l.lineTo(U.cx + U.x01, U.cy + U.y01), et < X ? l.arc(U.cx, U.cy, et, Pt(U.y01, U.x01), Pt(x.y01, x.x01), !d) : (l.arc(U.cx, U.cy, et, Pt(U.y01, U.x01), Pt(U.y11, U.x11), !d), l.arc(0, 0, h, Pt(U.cy + U.y11, U.cx + U.x11), Pt(x.cy + x.y11, x.cx + x.x11), d), l.arc(x.cx, x.cy, et, Pt(x.y11, x.x11), Pt(x.y01, x.x01), !d))) : l.arc(0, 0, h, k, y, d);
    }
    if (l.closePath(), a) return l = null, a + "" || null;
  }
  return c.centroid = function() {
    var a = (+t.apply(this, arguments) + +e.apply(this, arguments)) / 2, f = (+i.apply(this, arguments) + +s.apply(this, arguments)) / 2 - Hn / 2;
    return [He(f) * a, ge(f) * a];
  }, c.innerRadius = function(a) {
    return arguments.length ? (t = typeof a == "function" ? a : Ht(+a), c) : t;
  }, c.outerRadius = function(a) {
    return arguments.length ? (e = typeof a == "function" ? a : Ht(+a), c) : e;
  }, c.cornerRadius = function(a) {
    return arguments.length ? (n = typeof a == "function" ? a : Ht(+a), c) : n;
  }, c.padRadius = function(a) {
    return arguments.length ? (r = a == null ? null : typeof a == "function" ? a : Ht(+a), c) : r;
  }, c.startAngle = function(a) {
    return arguments.length ? (i = typeof a == "function" ? a : Ht(+a), c) : i;
  }, c.endAngle = function(a) {
    return arguments.length ? (s = typeof a == "function" ? a : Ht(+a), c) : s;
  }, c.padAngle = function(a) {
    return arguments.length ? (o = typeof a == "function" ? a : Ht(+a), c) : o;
  }, c.context = function(a) {
    return arguments.length ? (l = a ?? null, c) : l;
  }, c;
}
function Pg(t) {
  return typeof t == "object" && "length" in t ? t : Array.from(t);
}
function _u(t) {
  this._context = t;
}
_u.prototype = {
  areaStart: function() {
    this._line = 0;
  },
  areaEnd: function() {
    this._line = NaN;
  },
  lineStart: function() {
    this._point = 0;
  },
  lineEnd: function() {
    (this._line || this._line !== 0 && this._point === 1) && this._context.closePath(), this._line = 1 - this._line;
  },
  point: function(t, e) {
    switch (t = +t, e = +e, this._point) {
      case 0:
        this._point = 1, this._line ? this._context.lineTo(t, e) : this._context.moveTo(t, e);
        break;
      case 1:
        this._point = 2;
      default:
        this._context.lineTo(t, e);
        break;
    }
  }
};
function Lg(t) {
  return new _u(t);
}
function Ig(t) {
  return t[0];
}
function $g(t) {
  return t[1];
}
function Og(t, e) {
  var n = Ht(!0), r = null, i = Lg, s = null, o = yu(l);
  t = typeof t == "function" ? t : t === void 0 ? Ig : Ht(t), e = typeof e == "function" ? e : e === void 0 ? $g : Ht(e);
  function l(u) {
    var c, a = (u = Pg(u)).length, f, h = !1, p;
    for (r == null && (s = i(p = o())), c = 0; c <= a; ++c)
      !(c < a && n(f = u[c], c, u)) === h && ((h = !h) ? s.lineStart() : s.lineEnd()), h && s.point(+t(f, c, u), +e(f, c, u));
    if (p) return s = null, p + "" || null;
  }
  return l.x = function(u) {
    return arguments.length ? (t = typeof u == "function" ? u : Ht(+u), l) : t;
  }, l.y = function(u) {
    return arguments.length ? (e = typeof u == "function" ? u : Ht(+u), l) : e;
  }, l.defined = function(u) {
    return arguments.length ? (n = typeof u == "function" ? u : Ht(!!u), l) : n;
  }, l.curve = function(u) {
    return arguments.length ? (i = u, r != null && (s = i(r)), l) : i;
  }, l.context = function(u) {
    return arguments.length ? (u == null ? r = s = null : s = i(r = u), l) : r;
  }, l;
}
const fr = (t) => () => t;
function Ag(t, {
  sourceEvent: e,
  target: n,
  transform: r,
  dispatch: i
}) {
  Object.defineProperties(this, {
    type: { value: t, enumerable: !0, configurable: !0 },
    sourceEvent: { value: e, enumerable: !0, configurable: !0 },
    target: { value: n, enumerable: !0, configurable: !0 },
    transform: { value: r, enumerable: !0, configurable: !0 },
    _: { value: i }
  });
}
function Se(t, e, n) {
  this.k = t, this.x = e, this.y = n;
}
Se.prototype = {
  constructor: Se,
  scale: function(t) {
    return t === 1 ? this : new Se(this.k * t, this.x, this.y);
  },
  translate: function(t, e) {
    return t === 0 & e === 0 ? this : new Se(this.k, this.x + this.k * t, this.y + this.k * e);
  },
  apply: function(t) {
    return [t[0] * this.k + this.x, t[1] * this.k + this.y];
  },
  applyX: function(t) {
    return t * this.k + this.x;
  },
  applyY: function(t) {
    return t * this.k + this.y;
  },
  invert: function(t) {
    return [(t[0] - this.x) / this.k, (t[1] - this.y) / this.k];
  },
  invertX: function(t) {
    return (t - this.x) / this.k;
  },
  invertY: function(t) {
    return (t - this.y) / this.k;
  },
  rescaleX: function(t) {
    return t.copy().domain(t.range().map(this.invertX, this).map(t.invert, t));
  },
  rescaleY: function(t) {
    return t.copy().domain(t.range().map(this.invertY, this).map(t.invert, t));
  },
  toString: function() {
    return "translate(" + this.x + "," + this.y + ") scale(" + this.k + ")";
  }
};
var vu = new Se(1, 0, 0);
Se.prototype;
function bi(t) {
  t.stopImmediatePropagation();
}
function kn(t) {
  t.preventDefault(), t.stopImmediatePropagation();
}
function Fg(t) {
  return (!t.ctrlKey || t.type === "wheel") && !t.button;
}
function jg() {
  var t = this;
  return t instanceof SVGElement ? (t = t.ownerSVGElement || t, t.hasAttribute("viewBox") ? (t = t.viewBox.baseVal, [[t.x, t.y], [t.x + t.width, t.y + t.height]]) : [[0, 0], [t.width.baseVal.value, t.height.baseVal.value]]) : [[0, 0], [t.clientWidth, t.clientHeight]];
}
function Ao() {
  return this.__zoom || vu;
}
function Bg(t) {
  return -t.deltaY * (t.deltaMode === 1 ? 0.05 : t.deltaMode ? 1 : 2e-3) * (t.ctrlKey ? 10 : 1);
}
function zg() {
  return navigator.maxTouchPoints || "ontouchstart" in this;
}
function Dg(t, e, n) {
  var r = t.invertX(e[0][0]) - n[0][0], i = t.invertX(e[1][0]) - n[1][0], s = t.invertY(e[0][1]) - n[0][1], o = t.invertY(e[1][1]) - n[1][1];
  return t.translate(
    i > r ? (r + i) / 2 : Math.min(0, r) || Math.max(0, i),
    o > s ? (s + o) / 2 : Math.min(0, s) || Math.max(0, o)
  );
}
function Vg() {
  var t = Fg, e = jg, n = Dg, r = Bg, i = zg, s = [0, 1 / 0], o = [[-1 / 0, -1 / 0], [1 / 0, 1 / 0]], l = 250, u = Nd, c = Kn("start", "zoom", "end"), a, f, h, p = 500, w = 150, _ = 0, v = 10;
  function d(x) {
    x.property("__zoom", Ao).on("wheel.zoom", G, { passive: !1 }).on("mousedown.zoom", Q).on("dblclick.zoom", X).filter(i).on("touchstart.zoom", et).on("touchmove.zoom", ot).on("touchend.zoom touchcancel.zoom", U).style("-webkit-tap-highlight-color", "rgba(0,0,0,0)");
  }
  d.transform = function(x, D, N, j) {
    var B = x.selection ? x.selection() : x;
    B.property("__zoom", Ao), x !== B ? k(x, D, N, j) : B.interrupt().each(function() {
      L(this, arguments).event(j).start().zoom(null, typeof D == "function" ? D.apply(this, arguments) : D).end();
    });
  }, d.scaleBy = function(x, D, N, j) {
    d.scaleTo(x, function() {
      var B = this.__zoom.k, Y = typeof D == "function" ? D.apply(this, arguments) : D;
      return B * Y;
    }, N, j);
  }, d.scaleTo = function(x, D, N, j) {
    d.transform(x, function() {
      var B = e.apply(this, arguments), Y = this.__zoom, J = N == null ? y(B) : typeof N == "function" ? N.apply(this, arguments) : N, it = Y.invert(J), lt = typeof D == "function" ? D.apply(this, arguments) : D;
      return n(P(T(Y, lt), J, it), B, o);
    }, N, j);
  }, d.translateBy = function(x, D, N, j) {
    d.transform(x, function() {
      return n(this.__zoom.translate(
        typeof D == "function" ? D.apply(this, arguments) : D,
        typeof N == "function" ? N.apply(this, arguments) : N
      ), e.apply(this, arguments), o);
    }, null, j);
  }, d.translateTo = function(x, D, N, j, B) {
    d.transform(x, function() {
      var Y = e.apply(this, arguments), J = this.__zoom, it = j == null ? y(Y) : typeof j == "function" ? j.apply(this, arguments) : j;
      return n(vu.translate(it[0], it[1]).scale(J.k).translate(
        typeof D == "function" ? -D.apply(this, arguments) : -D,
        typeof N == "function" ? -N.apply(this, arguments) : -N
      ), Y, o);
    }, j, B);
  };
  function T(x, D) {
    return D = Math.max(s[0], Math.min(s[1], D)), D === x.k ? x : new Se(D, x.x, x.y);
  }
  function P(x, D, N) {
    var j = D[0] - N[0] * x.k, B = D[1] - N[1] * x.k;
    return j === x.x && B === x.y ? x : new Se(x.k, j, B);
  }
  function y(x) {
    return [(+x[0][0] + +x[1][0]) / 2, (+x[0][1] + +x[1][1]) / 2];
  }
  function k(x, D, N, j) {
    x.on("start.zoom", function() {
      L(this, arguments).event(j).start();
    }).on("interrupt.zoom end.zoom", function() {
      L(this, arguments).event(j).end();
    }).tween("zoom", function() {
      var B = this, Y = arguments, J = L(B, Y).event(j), it = e.apply(B, Y), lt = N == null ? y(it) : typeof N == "function" ? N.apply(B, Y) : N, kt = Math.max(it[1][0] - it[0][0], it[1][1] - it[0][1]), pt = B.__zoom, gt = typeof D == "function" ? D.apply(B, Y) : D, mt = u(pt.invert(lt).concat(kt / pt.k), gt.invert(lt).concat(kt / gt.k));
      return function(xt) {
        if (xt === 1) xt = gt;
        else {
          var Mt = mt(xt), Dt = kt / Mt[2];
          xt = new Se(Dt, lt[0] - Mt[0] * Dt, lt[1] - Mt[1] * Dt);
        }
        J.zoom(null, xt);
      };
    });
  }
  function L(x, D, N) {
    return !N && x.__zooming || new z(x, D);
  }
  function z(x, D) {
    this.that = x, this.args = D, this.active = 0, this.sourceEvent = null, this.extent = e.apply(x, D), this.taps = 0;
  }
  z.prototype = {
    event: function(x) {
      return x && (this.sourceEvent = x), this;
    },
    start: function() {
      return ++this.active === 1 && (this.that.__zooming = this, this.emit("start")), this;
    },
    zoom: function(x, D) {
      return this.mouse && x !== "mouse" && (this.mouse[1] = D.invert(this.mouse[0])), this.touch0 && x !== "touch" && (this.touch0[1] = D.invert(this.touch0[0])), this.touch1 && x !== "touch" && (this.touch1[1] = D.invert(this.touch1[0])), this.that.__zoom = D, this.emit("zoom"), this;
    },
    end: function() {
      return --this.active === 0 && (delete this.that.__zooming, this.emit("end")), this;
    },
    emit: function(x) {
      var D = Et(this.that).datum();
      c.call(
        x,
        this.that,
        new Ag(x, {
          sourceEvent: this.sourceEvent,
          target: d,
          type: x,
          transform: this.that.__zoom,
          dispatch: c
        }),
        D
      );
    }
  };
  function G(x, ...D) {
    if (!t.apply(this, arguments)) return;
    var N = L(this, D).event(x), j = this.__zoom, B = Math.max(s[0], Math.min(s[1], j.k * Math.pow(2, r.apply(this, arguments)))), Y = ne(x);
    if (N.wheel)
      (N.mouse[0][0] !== Y[0] || N.mouse[0][1] !== Y[1]) && (N.mouse[1] = j.invert(N.mouse[0] = Y)), clearTimeout(N.wheel);
    else {
      if (j.k === B) return;
      N.mouse = [Y, j.invert(Y)], Mr(this), N.start();
    }
    kn(x), N.wheel = setTimeout(J, w), N.zoom("mouse", n(P(T(j, B), N.mouse[0], N.mouse[1]), N.extent, o));
    function J() {
      N.wheel = null, N.end();
    }
  }
  function Q(x, ...D) {
    if (h || !t.apply(this, arguments)) return;
    var N = x.currentTarget, j = L(this, D, !0).event(x), B = Et(x.view).on("mousemove.zoom", lt, !0).on("mouseup.zoom", kt, !0), Y = ne(x, N), J = x.clientX, it = x.clientY;
    nu(x.view), bi(x), j.mouse = [Y, this.__zoom.invert(Y)], Mr(this), j.start();
    function lt(pt) {
      if (kn(pt), !j.moved) {
        var gt = pt.clientX - J, mt = pt.clientY - it;
        j.moved = gt * gt + mt * mt > _;
      }
      j.event(pt).zoom("mouse", n(P(j.that.__zoom, j.mouse[0] = ne(pt, N), j.mouse[1]), j.extent, o));
    }
    function kt(pt) {
      B.on("mousemove.zoom mouseup.zoom", null), ru(pt.view, j.moved), kn(pt), j.event(pt).end();
    }
  }
  function X(x, ...D) {
    if (t.apply(this, arguments)) {
      var N = this.__zoom, j = ne(x.changedTouches ? x.changedTouches[0] : x, this), B = N.invert(j), Y = N.k * (x.shiftKey ? 0.5 : 2), J = n(P(T(N, Y), j, B), e.apply(this, D), o);
      kn(x), l > 0 ? Et(this).transition().duration(l).call(k, J, j, x) : Et(this).call(d.transform, J, j, x);
    }
  }
  function et(x, ...D) {
    if (t.apply(this, arguments)) {
      var N = x.touches, j = N.length, B = L(this, D, x.changedTouches.length === j).event(x), Y, J, it, lt;
      for (bi(x), J = 0; J < j; ++J)
        it = N[J], lt = ne(it, this), lt = [lt, this.__zoom.invert(lt), it.identifier], B.touch0 ? !B.touch1 && B.touch0[2] !== lt[2] && (B.touch1 = lt, B.taps = 0) : (B.touch0 = lt, Y = !0, B.taps = 1 + !!a);
      a && (a = clearTimeout(a)), Y && (B.taps < 2 && (f = lt[0], a = setTimeout(function() {
        a = null;
      }, p)), Mr(this), B.start());
    }
  }
  function ot(x, ...D) {
    if (this.__zooming) {
      var N = L(this, D).event(x), j = x.changedTouches, B = j.length, Y, J, it, lt;
      for (kn(x), Y = 0; Y < B; ++Y)
        J = j[Y], it = ne(J, this), N.touch0 && N.touch0[2] === J.identifier ? N.touch0[0] = it : N.touch1 && N.touch1[2] === J.identifier && (N.touch1[0] = it);
      if (J = N.that.__zoom, N.touch1) {
        var kt = N.touch0[0], pt = N.touch0[1], gt = N.touch1[0], mt = N.touch1[1], xt = (xt = gt[0] - kt[0]) * xt + (xt = gt[1] - kt[1]) * xt, Mt = (Mt = mt[0] - pt[0]) * Mt + (Mt = mt[1] - pt[1]) * Mt;
        J = T(J, Math.sqrt(xt / Mt)), it = [(kt[0] + gt[0]) / 2, (kt[1] + gt[1]) / 2], lt = [(pt[0] + mt[0]) / 2, (pt[1] + mt[1]) / 2];
      } else if (N.touch0) it = N.touch0[0], lt = N.touch0[1];
      else return;
      N.zoom("touch", n(P(J, it, lt), N.extent, o));
    }
  }
  function U(x, ...D) {
    if (this.__zooming) {
      var N = L(this, D).event(x), j = x.changedTouches, B = j.length, Y, J;
      for (bi(x), h && clearTimeout(h), h = setTimeout(function() {
        h = null;
      }, p), Y = 0; Y < B; ++Y)
        J = j[Y], N.touch0 && N.touch0[2] === J.identifier ? delete N.touch0 : N.touch1 && N.touch1[2] === J.identifier && delete N.touch1;
      if (N.touch1 && !N.touch0 && (N.touch0 = N.touch1, delete N.touch1), N.touch0) N.touch0[1] = this.__zoom.invert(N.touch0[0]);
      else if (N.end(), N.taps === 2 && (J = ne(J, this), Math.hypot(f[0] - J[0], f[1] - J[1]) < v)) {
        var it = Et(this).on("dblclick.zoom");
        it && it.apply(this, arguments);
      }
    }
  }
  return d.wheelDelta = function(x) {
    return arguments.length ? (r = typeof x == "function" ? x : fr(+x), d) : r;
  }, d.filter = function(x) {
    return arguments.length ? (t = typeof x == "function" ? x : fr(!!x), d) : t;
  }, d.touchable = function(x) {
    return arguments.length ? (i = typeof x == "function" ? x : fr(!!x), d) : i;
  }, d.extent = function(x) {
    return arguments.length ? (e = typeof x == "function" ? x : fr([[+x[0][0], +x[0][1]], [+x[1][0], +x[1][1]]]), d) : e;
  }, d.scaleExtent = function(x) {
    return arguments.length ? (s[0] = +x[0], s[1] = +x[1], d) : [s[0], s[1]];
  }, d.translateExtent = function(x) {
    return arguments.length ? (o[0][0] = +x[0][0], o[1][0] = +x[1][0], o[0][1] = +x[0][1], o[1][1] = +x[1][1], d) : [[o[0][0], o[0][1]], [o[1][0], o[1][1]]];
  }, d.constrain = function(x) {
    return arguments.length ? (n = x, d) : n;
  }, d.duration = function(x) {
    return arguments.length ? (l = +x, d) : l;
  }, d.interpolate = function(x) {
    return arguments.length ? (u = x, d) : u;
  }, d.on = function() {
    var x = c.on.apply(c, arguments);
    return x === c ? d : x;
  }, d.clickDistance = function(x) {
    return arguments.length ? (_ = (x = +x) * x, d) : Math.sqrt(_);
  }, d.tapDistance = function(x) {
    return arguments.length ? (v = +x, d) : v;
  }, d;
}
function qg(t, e) {
  let n = Vg().filter((r) => {
    var i;
    return r.button === 0 || ((i = r.touches) == null ? void 0 : i.length) >= 2;
  });
  return Gg(n, t, e);
}
function Gg(t, e, n) {
  return n ? t.scaleExtent([0.5, 5]).on("zoom", (r) => e(r, !0)) : t.scaleExtent([1, 1]).on("zoom", (r) => e(r, !1));
}
class bu {
  /**
   * @param id - The internal ID which is used for node referencing.
   * @param idImported - The external ID provided for imported nodes (solely used for the purpose of imported node creation).
   * @param x - The nodes x position
   * @param y - The nodes y position
   * @param label - The nodes label
   * @param color - The color of the node which was set (for default color this is empty)
   * @param fixedPosition - A fixed node can't be dragged via GUI and isn't influenced by the simulation forces
   * @param deletable - If the node is deletable via GUI
   * @param labelEditable - If the nodes label is editable via GUI
   * @param allowIncomingLinks - If the node can get new incoming links via GUI
   * @param allowOutgoingLinks - If the node can get new outgoing links via GUI
   */
  constructor(e, n, r, i, s, o, l, u, c, a, f) {
    yt(this, "fx");
    yt(this, "fy");
    yt(this, "_fixedPosition");
    this.id = e, this.idImported = n, this.x = r, this.y = i, this.label = s, this.color = o, this.deletable = u, this.labelEditable = c, this.allowIncomingLinks = a, this.allowOutgoingLinks = f, this.fixedPosition = l;
  }
  set fixedPosition(e) {
    var n, r;
    this._fixedPosition = e, this.fx = (n = this.fixedPosition) != null && n.x ? this.x : void 0, this.fy = (r = this.fixedPosition) != null && r.y ? this.y : void 0;
  }
  get fixedPosition() {
    return this._fixedPosition;
  }
}
function Hg(t, e) {
  const n = new CustomEvent("nodecreated", {
    detail: {
      node: { id: t.id, label: t.label, x: t.x, y: t.y }
    }
  });
  e.node().dispatchEvent(n);
}
function Ug(t, e) {
  const n = new CustomEvent("linkcreated", {
    detail: {
      link: { id: t.id, label: t.label }
    }
  });
  e.node().dispatchEvent(n);
}
function Wg(t, e, n) {
  const r = new CustomEvent("nodeclicked", {
    detail: {
      node: { id: t.id, label: t.label, x: t.x, y: t.y },
      button: e
    }
  });
  n.node().dispatchEvent(r);
}
function Kg(t, e, n) {
  const r = new CustomEvent("linkclicked", {
    detail: {
      link: { id: t.id, label: t.label },
      button: e
    }
  });
  n.node().dispatchEvent(r);
}
function xi(t, e) {
  const n = new CustomEvent("nodedeleted", {
    detail: {
      node: { id: t.id, label: t.label, x: t.x, y: t.y }
    }
  });
  e.node().dispatchEvent(n);
}
function Mn(t, e) {
  const n = new CustomEvent("linkdeleted", {
    detail: {
      link: { id: t.id, label: t.label }
    }
  });
  e.node().dispatchEvent(n);
}
function Xg(t, e, n) {
  const r = new CustomEvent("labeledited", {
    detail: {
      parent: { id: t.id },
      label: e
    }
  });
  n.node().dispatchEvent(r);
}
function ee(t) {
  t.preventDefault(), t.stopPropagation();
}
function Yg(t, e, n, r) {
  return Jh().filter(
    (i, s) => {
      var o, l;
      return i.button === 0 && //left mouse click
      (((o = s.fixedPosition) == null ? void 0 : o.x) !== !0 || ((l = s.fixedPosition) == null ? void 0 : l.y) !== !0);
    }
  ).on("start", (i, s) => {
    var o, l;
    ee(i.sourceEvent), i.active === 0 && t.alphaTarget(0.5).restart(), ((o = s.fixedPosition) == null ? void 0 : o.x) !== !0 && (s.fx = s.x), ((l = s.fixedPosition) == null ? void 0 : l.y) !== !0 && (s.fy = s.y);
  }).on("drag", (i, s) => {
    var o, l;
    ((o = s.fixedPosition) == null ? void 0 : o.x) !== !0 && (s.fx = Math.max(r, Math.min(e - r, i.x))), ((l = s.fixedPosition) == null ? void 0 : l.y) !== !0 && (s.fy = Math.max(r, Math.min(n - r, i.y)));
  }).on("end", (i, s) => {
    var o, l;
    i.active === 0 && t.alphaTarget(0), ((o = s.fixedPosition) == null ? void 0 : o.x) !== !0 && (s.fx = void 0), ((l = s.fixedPosition) == null ? void 0 : l.y) !== !0 && (s.fy = void 0);
  });
}
function Jg(t, e, n, r, i) {
  const s = t.append("svg").attr("width", "100%").attr("height", "100%").on("pointermove", (o) => n(o)).on("pointerup", (o) => r(o)).on("contextmenu", (o) => ee(o)).on("dblclick", (o) => i(o)).call(e).on("dblclick.zoom", null).append("g");
  return s.append("rect").attr("width", "100%").attr("height", "100%").attr("fill", "white"), s;
}
var Xt = /* @__PURE__ */ ((t) => (t.LINE = "LINE", t.LINEREVERSE = "LINE-REVERSE", t.ARC = "ARC", t.ARCREVERSE = "ARC-REVERSE", t.REFLEXIVE = "REFLEXIVE", t))(Xt || {});
class Qg {
  // eslint-disable-next-line no-useless-constructor
  /**
   *
   * @param source
   * @param target
   * @param pathType
   * @param label
   * @param color The color of the node which was set (for default color this is empty)
   * @param deletable - If the link is deletable via GUI
   * @param labelEditable - If the link label is editable via GUI
   */
  constructor(e, n, r, i, s, o, l) {
    yt(this, "id");
    this.source = e, this.target = n, this.pathType = r, this.label = i, this.color = s, this.deletable = o, this.labelEditable = l, this.id = `${e.id}-${n.id}`;
  }
}
function Zg(t) {
  return t.append("g").classed("links", !0).selectAll("path");
}
function tm(t) {
  return t.append("g").classed("nodes", !0).selectAll("circle");
}
function hr(t) {
  let e = [], n = [];
  if (!Array.isArray(t))
    typeof t == "number" ? e = [t] : n = [t];
  else {
    let r = t.map(String);
    n = r.filter((i) => i.includes("-")), e = r.filter((i) => !i.includes("-")).map(Number);
  }
  return [e, n];
}
function dr(t, e) {
  e !== void 0 && (typeof e == "boolean" ? e ? t.fixedPosition = { x: !0, y: !0 } : t.fixedPosition = { x: !1, y: !1 } : em(["x", "y"], Object.keys(e)) && (t.fixedPosition = e, Ki(["x", "y"], Object.keys(e))));
}
function Un(t) {
  return t.replace(/([#.,;:<>+~^$|[\]()\\/])/g, "\\$1");
}
function Fo(t) {
  let e = t.target;
  e.hasPointerCapture(t.pointerId) && e.releasePointerCapture(t.pointerId);
}
function Ki(t, e, n) {
  let r = !0;
  return e.forEach((i) => {
    t.includes(
      i
      // we actually just check if the type is keyof
    ) || (r = !1, $n(
      `Option not valid: ${i}`,
      `Use the following: ${t.join(", ")}.`
    ));
  }), r;
}
function em(t, e, n) {
  let r = !0, i = t.filter((s) => !e.includes(s));
  return i.length > 0 && (r = !1, $n("Option missing", `Add: ${i}`)), r;
}
function $n(t, e) {
  console.error(t + `
` + e);
}
function nm(t, e, n, r) {
  if (On(t, n, e + "-link-arrow", "graph-controller__arrow", !1), On(
    t,
    n,
    e + "-link-arrow-reverse",
    "graph-controller__arrow",
    !0
  ), On(
    t,
    n,
    e + "-draggable-link-arrow",
    "graph-controller__arrow draggable",
    !1
  ), r)
    for (let i of r)
      Xi(t, e, n, i);
}
function Xi(t, e, n, r) {
  t.select(`#${e}-link-arrow-` + Un(r)).empty() && (On(
    t,
    n,
    e + "-link-arrow-" + r,
    "graph-controller__arrow " + r,
    !1,
    r
  ), On(
    t,
    n,
    e + "-link-arrow-reverse-" + r,
    "graph-controller__arrow colored",
    !0,
    r
  ));
}
function Ei(t, e, n) {
  t.select(`#${e}-link-arrow-` + Un(n)).select(function() {
    return this.parentNode;
  }).remove(), t.select(`#${e}-link-arrow-reverse-` + Un(n)).select(function() {
    return this.parentNode;
  }).remove();
}
function On(t, e, n, r, i, s) {
  t.append("defs").append("marker").attr("id", n).attr("viewBox", e.markerPath).attr("refX", e.markerRef).attr("refY", e.markerRef).attr("markerWidth", e.markerBoxSize).attr("markerHeight", e.markerBoxSize).attr("orient", i ? "auto-start-reverse" : "auto").classed(r, !0).append("path").attr("d", `${Og()(e.arrowPoints)}`).style("fill", s || "");
}
function rm(t) {
  return t.append("path").classed("graph-controller__link draggable hidden", !0).attr("d", "M0,0L0,0");
}
class jo {
  constructor() {
    yt(this, "nodeIdCounter", 0);
    yt(this, "nodes", []);
    yt(this, "links", []);
  }
  createNode(e, n, r, i, s, o = { x: !1, y: !1 }, l = !0, u = !0, c = !0, a = !0) {
    const f = new bu(
      this.nodeIdCounter++,
      r,
      e,
      n,
      i,
      s,
      o,
      l,
      u,
      c,
      a
    );
    return this.nodes.push(f), f;
  }
  createLink(e, n, r, i, s = !0, o = !0) {
    if (this.links.find(
      (f) => f.source.id === e && f.target.id === n
    ) !== void 0)
      return;
    const u = this.nodes.find((f) => f.id === e);
    if (u === void 0)
      return;
    const c = this.nodes.find((f) => f.id === n);
    if (c === void 0)
      return;
    const a = new Qg(
      u,
      c,
      void 0,
      r,
      i,
      s,
      o
    );
    return this.links.push(a), a;
  }
  removeNode(e) {
    const n = this.nodes.findIndex((i) => i.id === e.id);
    if (n === -1)
      return;
    this.nodes.splice(n, 1);
    const r = this.links.filter(
      (i) => i.source.id === e.id || i.target.id === e.id
    );
    return r.forEach((i) => {
      const s = this.links.indexOf(i, 0);
      this.links.splice(s, 1);
    }), [e, r];
  }
  removeLink(e) {
    const n = this.links.findIndex(
      (r) => r.source.id === e.source.id && r.target.id === e.target.id
    );
    if (n !== -1)
      return this.links.splice(n, 1), e;
  }
  /**
   * Checks if a link in a given (not default) color exists.
   * @param color - Color to check on.
   * @param excludedLinkId - You can optionally exclude one or more links via their ID from this check
   * @returns True if non-default colored links exist, false otherwise.
   */
  hasNonDefaultLinkColor(e, n = "") {
    return this.links.some((r) => r.color === e && r.id !== n);
  }
  /**
   * Get the existing non-default colors of links.
   * @returns An array of strings representing non-default colors, empty if none exist.
   */
  getNonDefaultLinkColors() {
    return this.links.map((e) => e.color).filter((e) => e !== void 0 && e !== "");
  }
  /**
   * Get the link ids of links with provided color.
   * @param color - Color to check on.
   * @param excludedLinkId - You can optionally exclude a link from this check via its ID
   * @returns An array of link IDs that have the provided color (without the excludedLinkId)
   */
  getLinkIdsWithNonDefaultLinkColors(e, n = "") {
    return this.links.filter((r) => r.color === e && r.id !== n).map((r) => r.id);
  }
  /** Formats the graph in trivial graph format.
   * @param includeNodeLabels if node labels should be included
   * @param includeLinkLabels if link labels should be included
   * @param includeNodeColor TGF normally has no color option, this is just used for internal purposes
   * @param includeLinkColor TGF normally has no color option, this is just used for internal purposes
   * @returns The graph in TGF format
   */
  toTGF(e = !0, n = !0, r = !1, i = !1) {
    if (this.nodes.length === 0 && this.links.length === 0)
      return "";
    let s, o;
    return s = this.nodes.map((l) => {
      let u = `${l.id}`;
      return e && l.label !== void 0 && (u += ` ${l.label}`), r && l.color !== void 0 && (u += ` /COLOR:/${l.color}`), u;
    }).join(`
`), o = this.links.map((l) => {
      let u = `${l.source.id} ${l.target.id}`;
      return n && l.label !== void 0 && (u += ` ${l.label}`), i && l.color !== void 0 && (u += ` /COLOR:/${l.color}`), u;
    }).join(`
`), `${s}${o ? `
#
` : ""}${o}`;
  }
  /** Formats the graph in a json like graph format.
   * @param includeNodeLabels if node labels should be included
   * @param includeLinkLabels if link labels should be included
   * @param includeNodeColor if node color should be included
   * @param includeLinkColor if link color should be included
   * @param includeNodePosition if position should be included
   * @param includeNodeEditability if editability of node via GUI should be included
   * @param includeLinkEditability if editability of link via GUI should be included
   * @returns The graph in JSON format*/
  toJSON(e = !0, n = !0, r = !0, i = !0, s = !0, o = !0, l = !0) {
    let u = this.nodes.map((a) => {
      let f = ["id"];
      return e && a.label !== void 0 && f.push("label"), r && a.color !== void 0 && f.push("color"), s && a.x !== void 0 && a.y !== void 0 && (f.push("x"), f.push("y")), o && (a.fixedPosition !== void 0 && f.push("fixedPosition"), a.deletable !== void 0 && f.push("deletable"), a.labelEditable !== void 0 && f.push("labelEditable"), a.allowIncomingLinks !== void 0 && f.push("allowIncomingLinks"), a.allowOutgoingLinks !== void 0 && f.push("allowOutgoingLinks")), JSON.stringify(a, f);
    }).join(`,
		`), c = this.links.map((a) => {
      let f = ["sourceId", "targetId"];
      n && a.label !== void 0 && f.push("label"), i && a.color !== void 0 && f.push("color"), l && (a.deletable !== void 0 && f.push("deletable"), a.labelEditable !== void 0 && f.push("labelEditable"));
      let h = this._convertToJSONLink(a);
      return JSON.stringify(h, f);
    }).join(`,
		`);
    return `{
	"nodes":[
		${u}
	],
	"links":[
		${c}
	]
}`;
  }
  _convertToJSONLink(e) {
    let n = e.id.split("-");
    return {
      sourceId: Number(n[0]),
      targetId: Number(n[1]),
      label: e.label,
      color: e.color,
      deletable: e.deletable,
      labelEditable: e.labelEditable
    };
  }
}
function im(t, e, n, r, i) {
  let s = wg(t.nodes).on("tick", () => i()).force(
    "collision",
    lg().radius(e.nodeRadius)
    //stop overlapping
  );
  return s = sm(t, s, n, r, e), s = Eu(s, t, e, e.fixedLinkDistanceEnabled), s = xu(s, e.nodePhysicsEnabled, n, r), s;
}
function sm(t, e, n, r, i) {
  return e.force("bounds", () => {
    for (const s of t.nodes)
      s.x = Math.max(i.nodeRadius, Math.min(n - i.nodeRadius, s.x)), s.y = Math.max(i.nodeRadius, Math.min(r - i.nodeRadius, s.y));
  });
}
function xu(t, e, n, r) {
  return e ? t.force("charge", yg().strength(-500)).force("x", _g(n / 2).strength(0.05)).force("y", vg(r / 2).strength(0.05)) : t.force("charge", null).force("x", null).force("y", null);
}
function Eu(t, e, n, r) {
  return r ? t.force(
    "link",
    cg().links(e.links).id((i) => i.id).distance(n.nodeRadius * 10)
  ) : t.force("link", null);
}
const om = Object.prototype.toString;
function Vr(t) {
  const e = om.call(t);
  return e.endsWith("Array]") && !e.includes("Big");
}
function lm(t) {
  var e = arguments.length > 1 && arguments[1] !== void 0 ? arguments[1] : {};
  if (!Vr(t))
    throw new TypeError("input must be an array");
  if (t.length === 0)
    throw new TypeError("input must not be empty");
  var n = e.fromIndex, r = n === void 0 ? 0 : n, i = e.toIndex, s = i === void 0 ? t.length : i;
  if (r < 0 || r >= t.length || !Number.isInteger(r))
    throw new Error("fromIndex must be a positive integer smaller than length");
  if (s <= r || s > t.length || !Number.isInteger(s))
    throw new Error("toIndex must be an integer greater than fromIndex and at most equal to length");
  for (var o = t[r], l = r + 1; l < s; l++)
    t[l] > o && (o = t[l]);
  return o;
}
function um(t) {
  var e = arguments.length > 1 && arguments[1] !== void 0 ? arguments[1] : {};
  if (!Vr(t))
    throw new TypeError("input must be an array");
  if (t.length === 0)
    throw new TypeError("input must not be empty");
  var n = e.fromIndex, r = n === void 0 ? 0 : n, i = e.toIndex, s = i === void 0 ? t.length : i;
  if (r < 0 || r >= t.length || !Number.isInteger(r))
    throw new Error("fromIndex must be a positive integer smaller than length");
  if (s <= r || s > t.length || !Number.isInteger(s))
    throw new Error("toIndex must be an integer greater than fromIndex and at most equal to length");
  for (var o = t[r], l = r + 1; l < s; l++)
    t[l] < o && (o = t[l]);
  return o;
}
function Bo(t) {
  var e = arguments.length > 1 && arguments[1] !== void 0 ? arguments[1] : {};
  if (Vr(t)) {
    if (t.length === 0)
      throw new TypeError("input must not be empty");
  } else throw new TypeError("input must be an array");
  var n;
  if (e.output !== void 0) {
    if (!Vr(e.output))
      throw new TypeError("output option must be an array if specified");
    n = e.output;
  } else
    n = new Array(t.length);
  var r = um(t), i = lm(t);
  if (r === i)
    throw new RangeError("minimum and maximum input values are equal. Cannot rescale a constant array");
  var s = e.min, o = s === void 0 ? e.autoMinMax ? r : 0 : s, l = e.max, u = l === void 0 ? e.autoMinMax ? i : 1 : l;
  if (o >= u)
    throw new RangeError("min option must be smaller than max option");
  for (var c = (u - o) / (i - r), a = 0; a < t.length; a++)
    n[a] = (t[a] - r) * c + o;
  return n;
}
const pr = " ".repeat(2), Su = " ".repeat(4);
function cm() {
  return ku(this);
}
function ku(t, e = {}) {
  const { maxRows: n = 15, maxColumns: r = 10, maxNumSize: i = 8 } = e;
  return `${t.constructor.name} {
${pr}[
${Su}${am(t, n, r, i)}
${pr}]
${pr}rows: ${t.rows}
${pr}columns: ${t.columns}
}`;
}
function am(t, e, n, r) {
  const { rows: i, columns: s } = t, o = Math.min(i, e), l = Math.min(s, n), u = [];
  for (let c = 0; c < o; c++) {
    let a = [];
    for (let f = 0; f < l; f++)
      a.push(fm(t.get(c, f), r));
    u.push(`${a.join(" ")}`);
  }
  return l !== s && (u[u.length - 1] += ` ... ${s - n} more columns`), o !== i && u.push(`... ${i - e} more rows`), u.join(`
${Su}`);
}
function fm(t, e) {
  const n = String(t);
  if (n.length <= e)
    return n.padEnd(e, " ");
  const r = t.toPrecision(e - 2);
  if (r.length <= e)
    return r;
  const i = t.toExponential(e - 2), s = i.indexOf("e"), o = i.slice(s);
  return i.slice(0, e - o.length) + o;
}
function hm(t, e) {
  t.prototype.add = function(r) {
    return typeof r == "number" ? this.addS(r) : this.addM(r);
  }, t.prototype.addS = function(r) {
    for (let i = 0; i < this.rows; i++)
      for (let s = 0; s < this.columns; s++)
        this.set(i, s, this.get(i, s) + r);
    return this;
  }, t.prototype.addM = function(r) {
    if (r = e.checkMatrix(r), this.rows !== r.rows || this.columns !== r.columns)
      throw new RangeError("Matrices dimensions must be equal");
    for (let i = 0; i < this.rows; i++)
      for (let s = 0; s < this.columns; s++)
        this.set(i, s, this.get(i, s) + r.get(i, s));
    return this;
  }, t.add = function(r, i) {
    return new e(r).add(i);
  }, t.prototype.sub = function(r) {
    return typeof r == "number" ? this.subS(r) : this.subM(r);
  }, t.prototype.subS = function(r) {
    for (let i = 0; i < this.rows; i++)
      for (let s = 0; s < this.columns; s++)
        this.set(i, s, this.get(i, s) - r);
    return this;
  }, t.prototype.subM = function(r) {
    if (r = e.checkMatrix(r), this.rows !== r.rows || this.columns !== r.columns)
      throw new RangeError("Matrices dimensions must be equal");
    for (let i = 0; i < this.rows; i++)
      for (let s = 0; s < this.columns; s++)
        this.set(i, s, this.get(i, s) - r.get(i, s));
    return this;
  }, t.sub = function(r, i) {
    return new e(r).sub(i);
  }, t.prototype.subtract = t.prototype.sub, t.prototype.subtractS = t.prototype.subS, t.prototype.subtractM = t.prototype.subM, t.subtract = t.sub, t.prototype.mul = function(r) {
    return typeof r == "number" ? this.mulS(r) : this.mulM(r);
  }, t.prototype.mulS = function(r) {
    for (let i = 0; i < this.rows; i++)
      for (let s = 0; s < this.columns; s++)
        this.set(i, s, this.get(i, s) * r);
    return this;
  }, t.prototype.mulM = function(r) {
    if (r = e.checkMatrix(r), this.rows !== r.rows || this.columns !== r.columns)
      throw new RangeError("Matrices dimensions must be equal");
    for (let i = 0; i < this.rows; i++)
      for (let s = 0; s < this.columns; s++)
        this.set(i, s, this.get(i, s) * r.get(i, s));
    return this;
  }, t.mul = function(r, i) {
    return new e(r).mul(i);
  }, t.prototype.multiply = t.prototype.mul, t.prototype.multiplyS = t.prototype.mulS, t.prototype.multiplyM = t.prototype.mulM, t.multiply = t.mul, t.prototype.div = function(r) {
    return typeof r == "number" ? this.divS(r) : this.divM(r);
  }, t.prototype.divS = function(r) {
    for (let i = 0; i < this.rows; i++)
      for (let s = 0; s < this.columns; s++)
        this.set(i, s, this.get(i, s) / r);
    return this;
  }, t.prototype.divM = function(r) {
    if (r = e.checkMatrix(r), this.rows !== r.rows || this.columns !== r.columns)
      throw new RangeError("Matrices dimensions must be equal");
    for (let i = 0; i < this.rows; i++)
      for (let s = 0; s < this.columns; s++)
        this.set(i, s, this.get(i, s) / r.get(i, s));
    return this;
  }, t.div = function(r, i) {
    return new e(r).div(i);
  }, t.prototype.divide = t.prototype.div, t.prototype.divideS = t.prototype.divS, t.prototype.divideM = t.prototype.divM, t.divide = t.div, t.prototype.mod = function(r) {
    return typeof r == "number" ? this.modS(r) : this.modM(r);
  }, t.prototype.modS = function(r) {
    for (let i = 0; i < this.rows; i++)
      for (let s = 0; s < this.columns; s++)
        this.set(i, s, this.get(i, s) % r);
    return this;
  }, t.prototype.modM = function(r) {
    if (r = e.checkMatrix(r), this.rows !== r.rows || this.columns !== r.columns)
      throw new RangeError("Matrices dimensions must be equal");
    for (let i = 0; i < this.rows; i++)
      for (let s = 0; s < this.columns; s++)
        this.set(i, s, this.get(i, s) % r.get(i, s));
    return this;
  }, t.mod = function(r, i) {
    return new e(r).mod(i);
  }, t.prototype.modulus = t.prototype.mod, t.prototype.modulusS = t.prototype.modS, t.prototype.modulusM = t.prototype.modM, t.modulus = t.mod, t.prototype.and = function(r) {
    return typeof r == "number" ? this.andS(r) : this.andM(r);
  }, t.prototype.andS = function(r) {
    for (let i = 0; i < this.rows; i++)
      for (let s = 0; s < this.columns; s++)
        this.set(i, s, this.get(i, s) & r);
    return this;
  }, t.prototype.andM = function(r) {
    if (r = e.checkMatrix(r), this.rows !== r.rows || this.columns !== r.columns)
      throw new RangeError("Matrices dimensions must be equal");
    for (let i = 0; i < this.rows; i++)
      for (let s = 0; s < this.columns; s++)
        this.set(i, s, this.get(i, s) & r.get(i, s));
    return this;
  }, t.and = function(r, i) {
    return new e(r).and(i);
  }, t.prototype.or = function(r) {
    return typeof r == "number" ? this.orS(r) : this.orM(r);
  }, t.prototype.orS = function(r) {
    for (let i = 0; i < this.rows; i++)
      for (let s = 0; s < this.columns; s++)
        this.set(i, s, this.get(i, s) | r);
    return this;
  }, t.prototype.orM = function(r) {
    if (r = e.checkMatrix(r), this.rows !== r.rows || this.columns !== r.columns)
      throw new RangeError("Matrices dimensions must be equal");
    for (let i = 0; i < this.rows; i++)
      for (let s = 0; s < this.columns; s++)
        this.set(i, s, this.get(i, s) | r.get(i, s));
    return this;
  }, t.or = function(r, i) {
    return new e(r).or(i);
  }, t.prototype.xor = function(r) {
    return typeof r == "number" ? this.xorS(r) : this.xorM(r);
  }, t.prototype.xorS = function(r) {
    for (let i = 0; i < this.rows; i++)
      for (let s = 0; s < this.columns; s++)
        this.set(i, s, this.get(i, s) ^ r);
    return this;
  }, t.prototype.xorM = function(r) {
    if (r = e.checkMatrix(r), this.rows !== r.rows || this.columns !== r.columns)
      throw new RangeError("Matrices dimensions must be equal");
    for (let i = 0; i < this.rows; i++)
      for (let s = 0; s < this.columns; s++)
        this.set(i, s, this.get(i, s) ^ r.get(i, s));
    return this;
  }, t.xor = function(r, i) {
    return new e(r).xor(i);
  }, t.prototype.leftShift = function(r) {
    return typeof r == "number" ? this.leftShiftS(r) : this.leftShiftM(r);
  }, t.prototype.leftShiftS = function(r) {
    for (let i = 0; i < this.rows; i++)
      for (let s = 0; s < this.columns; s++)
        this.set(i, s, this.get(i, s) << r);
    return this;
  }, t.prototype.leftShiftM = function(r) {
    if (r = e.checkMatrix(r), this.rows !== r.rows || this.columns !== r.columns)
      throw new RangeError("Matrices dimensions must be equal");
    for (let i = 0; i < this.rows; i++)
      for (let s = 0; s < this.columns; s++)
        this.set(i, s, this.get(i, s) << r.get(i, s));
    return this;
  }, t.leftShift = function(r, i) {
    return new e(r).leftShift(i);
  }, t.prototype.signPropagatingRightShift = function(r) {
    return typeof r == "number" ? this.signPropagatingRightShiftS(r) : this.signPropagatingRightShiftM(r);
  }, t.prototype.signPropagatingRightShiftS = function(r) {
    for (let i = 0; i < this.rows; i++)
      for (let s = 0; s < this.columns; s++)
        this.set(i, s, this.get(i, s) >> r);
    return this;
  }, t.prototype.signPropagatingRightShiftM = function(r) {
    if (r = e.checkMatrix(r), this.rows !== r.rows || this.columns !== r.columns)
      throw new RangeError("Matrices dimensions must be equal");
    for (let i = 0; i < this.rows; i++)
      for (let s = 0; s < this.columns; s++)
        this.set(i, s, this.get(i, s) >> r.get(i, s));
    return this;
  }, t.signPropagatingRightShift = function(r, i) {
    return new e(r).signPropagatingRightShift(i);
  }, t.prototype.rightShift = function(r) {
    return typeof r == "number" ? this.rightShiftS(r) : this.rightShiftM(r);
  }, t.prototype.rightShiftS = function(r) {
    for (let i = 0; i < this.rows; i++)
      for (let s = 0; s < this.columns; s++)
        this.set(i, s, this.get(i, s) >>> r);
    return this;
  }, t.prototype.rightShiftM = function(r) {
    if (r = e.checkMatrix(r), this.rows !== r.rows || this.columns !== r.columns)
      throw new RangeError("Matrices dimensions must be equal");
    for (let i = 0; i < this.rows; i++)
      for (let s = 0; s < this.columns; s++)
        this.set(i, s, this.get(i, s) >>> r.get(i, s));
    return this;
  }, t.rightShift = function(r, i) {
    return new e(r).rightShift(i);
  }, t.prototype.zeroFillRightShift = t.prototype.rightShift, t.prototype.zeroFillRightShiftS = t.prototype.rightShiftS, t.prototype.zeroFillRightShiftM = t.prototype.rightShiftM, t.zeroFillRightShift = t.rightShift, t.prototype.not = function() {
    for (let r = 0; r < this.rows; r++)
      for (let i = 0; i < this.columns; i++)
        this.set(r, i, ~this.get(r, i));
    return this;
  }, t.not = function(r) {
    return new e(r).not();
  }, t.prototype.abs = function() {
    for (let r = 0; r < this.rows; r++)
      for (let i = 0; i < this.columns; i++)
        this.set(r, i, Math.abs(this.get(r, i)));
    return this;
  }, t.abs = function(r) {
    return new e(r).abs();
  }, t.prototype.acos = function() {
    for (let r = 0; r < this.rows; r++)
      for (let i = 0; i < this.columns; i++)
        this.set(r, i, Math.acos(this.get(r, i)));
    return this;
  }, t.acos = function(r) {
    return new e(r).acos();
  }, t.prototype.acosh = function() {
    for (let r = 0; r < this.rows; r++)
      for (let i = 0; i < this.columns; i++)
        this.set(r, i, Math.acosh(this.get(r, i)));
    return this;
  }, t.acosh = function(r) {
    return new e(r).acosh();
  }, t.prototype.asin = function() {
    for (let r = 0; r < this.rows; r++)
      for (let i = 0; i < this.columns; i++)
        this.set(r, i, Math.asin(this.get(r, i)));
    return this;
  }, t.asin = function(r) {
    return new e(r).asin();
  }, t.prototype.asinh = function() {
    for (let r = 0; r < this.rows; r++)
      for (let i = 0; i < this.columns; i++)
        this.set(r, i, Math.asinh(this.get(r, i)));
    return this;
  }, t.asinh = function(r) {
    return new e(r).asinh();
  }, t.prototype.atan = function() {
    for (let r = 0; r < this.rows; r++)
      for (let i = 0; i < this.columns; i++)
        this.set(r, i, Math.atan(this.get(r, i)));
    return this;
  }, t.atan = function(r) {
    return new e(r).atan();
  }, t.prototype.atanh = function() {
    for (let r = 0; r < this.rows; r++)
      for (let i = 0; i < this.columns; i++)
        this.set(r, i, Math.atanh(this.get(r, i)));
    return this;
  }, t.atanh = function(r) {
    return new e(r).atanh();
  }, t.prototype.cbrt = function() {
    for (let r = 0; r < this.rows; r++)
      for (let i = 0; i < this.columns; i++)
        this.set(r, i, Math.cbrt(this.get(r, i)));
    return this;
  }, t.cbrt = function(r) {
    return new e(r).cbrt();
  }, t.prototype.ceil = function() {
    for (let r = 0; r < this.rows; r++)
      for (let i = 0; i < this.columns; i++)
        this.set(r, i, Math.ceil(this.get(r, i)));
    return this;
  }, t.ceil = function(r) {
    return new e(r).ceil();
  }, t.prototype.clz32 = function() {
    for (let r = 0; r < this.rows; r++)
      for (let i = 0; i < this.columns; i++)
        this.set(r, i, Math.clz32(this.get(r, i)));
    return this;
  }, t.clz32 = function(r) {
    return new e(r).clz32();
  }, t.prototype.cos = function() {
    for (let r = 0; r < this.rows; r++)
      for (let i = 0; i < this.columns; i++)
        this.set(r, i, Math.cos(this.get(r, i)));
    return this;
  }, t.cos = function(r) {
    return new e(r).cos();
  }, t.prototype.cosh = function() {
    for (let r = 0; r < this.rows; r++)
      for (let i = 0; i < this.columns; i++)
        this.set(r, i, Math.cosh(this.get(r, i)));
    return this;
  }, t.cosh = function(r) {
    return new e(r).cosh();
  }, t.prototype.exp = function() {
    for (let r = 0; r < this.rows; r++)
      for (let i = 0; i < this.columns; i++)
        this.set(r, i, Math.exp(this.get(r, i)));
    return this;
  }, t.exp = function(r) {
    return new e(r).exp();
  }, t.prototype.expm1 = function() {
    for (let r = 0; r < this.rows; r++)
      for (let i = 0; i < this.columns; i++)
        this.set(r, i, Math.expm1(this.get(r, i)));
    return this;
  }, t.expm1 = function(r) {
    return new e(r).expm1();
  }, t.prototype.floor = function() {
    for (let r = 0; r < this.rows; r++)
      for (let i = 0; i < this.columns; i++)
        this.set(r, i, Math.floor(this.get(r, i)));
    return this;
  }, t.floor = function(r) {
    return new e(r).floor();
  }, t.prototype.fround = function() {
    for (let r = 0; r < this.rows; r++)
      for (let i = 0; i < this.columns; i++)
        this.set(r, i, Math.fround(this.get(r, i)));
    return this;
  }, t.fround = function(r) {
    return new e(r).fround();
  }, t.prototype.log = function() {
    for (let r = 0; r < this.rows; r++)
      for (let i = 0; i < this.columns; i++)
        this.set(r, i, Math.log(this.get(r, i)));
    return this;
  }, t.log = function(r) {
    return new e(r).log();
  }, t.prototype.log1p = function() {
    for (let r = 0; r < this.rows; r++)
      for (let i = 0; i < this.columns; i++)
        this.set(r, i, Math.log1p(this.get(r, i)));
    return this;
  }, t.log1p = function(r) {
    return new e(r).log1p();
  }, t.prototype.log10 = function() {
    for (let r = 0; r < this.rows; r++)
      for (let i = 0; i < this.columns; i++)
        this.set(r, i, Math.log10(this.get(r, i)));
    return this;
  }, t.log10 = function(r) {
    return new e(r).log10();
  }, t.prototype.log2 = function() {
    for (let r = 0; r < this.rows; r++)
      for (let i = 0; i < this.columns; i++)
        this.set(r, i, Math.log2(this.get(r, i)));
    return this;
  }, t.log2 = function(r) {
    return new e(r).log2();
  }, t.prototype.round = function() {
    for (let r = 0; r < this.rows; r++)
      for (let i = 0; i < this.columns; i++)
        this.set(r, i, Math.round(this.get(r, i)));
    return this;
  }, t.round = function(r) {
    return new e(r).round();
  }, t.prototype.sign = function() {
    for (let r = 0; r < this.rows; r++)
      for (let i = 0; i < this.columns; i++)
        this.set(r, i, Math.sign(this.get(r, i)));
    return this;
  }, t.sign = function(r) {
    return new e(r).sign();
  }, t.prototype.sin = function() {
    for (let r = 0; r < this.rows; r++)
      for (let i = 0; i < this.columns; i++)
        this.set(r, i, Math.sin(this.get(r, i)));
    return this;
  }, t.sin = function(r) {
    return new e(r).sin();
  }, t.prototype.sinh = function() {
    for (let r = 0; r < this.rows; r++)
      for (let i = 0; i < this.columns; i++)
        this.set(r, i, Math.sinh(this.get(r, i)));
    return this;
  }, t.sinh = function(r) {
    return new e(r).sinh();
  }, t.prototype.sqrt = function() {
    for (let r = 0; r < this.rows; r++)
      for (let i = 0; i < this.columns; i++)
        this.set(r, i, Math.sqrt(this.get(r, i)));
    return this;
  }, t.sqrt = function(r) {
    return new e(r).sqrt();
  }, t.prototype.tan = function() {
    for (let r = 0; r < this.rows; r++)
      for (let i = 0; i < this.columns; i++)
        this.set(r, i, Math.tan(this.get(r, i)));
    return this;
  }, t.tan = function(r) {
    return new e(r).tan();
  }, t.prototype.tanh = function() {
    for (let r = 0; r < this.rows; r++)
      for (let i = 0; i < this.columns; i++)
        this.set(r, i, Math.tanh(this.get(r, i)));
    return this;
  }, t.tanh = function(r) {
    return new e(r).tanh();
  }, t.prototype.trunc = function() {
    for (let r = 0; r < this.rows; r++)
      for (let i = 0; i < this.columns; i++)
        this.set(r, i, Math.trunc(this.get(r, i)));
    return this;
  }, t.trunc = function(r) {
    return new e(r).trunc();
  }, t.pow = function(r, i) {
    return new e(r).pow(i);
  }, t.prototype.pow = function(r) {
    return typeof r == "number" ? this.powS(r) : this.powM(r);
  }, t.prototype.powS = function(r) {
    for (let i = 0; i < this.rows; i++)
      for (let s = 0; s < this.columns; s++)
        this.set(i, s, Math.pow(this.get(i, s), r));
    return this;
  }, t.prototype.powM = function(r) {
    if (r = e.checkMatrix(r), this.rows !== r.rows || this.columns !== r.columns)
      throw new RangeError("Matrices dimensions must be equal");
    for (let i = 0; i < this.rows; i++)
      for (let s = 0; s < this.columns; s++)
        this.set(i, s, Math.pow(this.get(i, s), r.get(i, s)));
    return this;
  };
}
function oe(t, e, n) {
  let r = n ? t.rows : t.rows - 1;
  if (e < 0 || e > r)
    throw new RangeError("Row index out of range");
}
function le(t, e, n) {
  let r = n ? t.columns : t.columns - 1;
  if (e < 0 || e > r)
    throw new RangeError("Column index out of range");
}
function sn(t, e) {
  if (e.to1DArray && (e = e.to1DArray()), e.length !== t.columns)
    throw new RangeError(
      "vector size must be the same as the number of columns"
    );
  return e;
}
function on(t, e) {
  if (e.to1DArray && (e = e.to1DArray()), e.length !== t.rows)
    throw new RangeError("vector size must be the same as the number of rows");
  return e;
}
function dm(t, e, n) {
  return {
    row: pm(t, e),
    column: gm(t, n)
  };
}
function pm(t, e) {
  if (typeof e != "object")
    throw new TypeError("unexpected type for row indices");
  if (e.some((r) => r < 0 || r >= t.rows))
    throw new RangeError("row indices are out of range");
  return Array.isArray(e) || (e = Array.from(e)), e;
}
function gm(t, e) {
  if (typeof e != "object")
    throw new TypeError("unexpected type for column indices");
  if (e.some((r) => r < 0 || r >= t.columns))
    throw new RangeError("column indices are out of range");
  return Array.isArray(e) || (e = Array.from(e)), e;
}
function zo(t, e, n, r, i) {
  if (arguments.length !== 5)
    throw new RangeError("expected 4 arguments");
  if (gr("startRow", e), gr("endRow", n), gr("startColumn", r), gr("endColumn", i), e > n || r > i || e < 0 || e >= t.rows || n < 0 || n >= t.rows || r < 0 || r >= t.columns || i < 0 || i >= t.columns)
    throw new RangeError("Submatrix indices are out of range");
}
function ii(t, e = 0) {
  let n = [];
  for (let r = 0; r < t; r++)
    n.push(e);
  return n;
}
function gr(t, e) {
  if (typeof e != "number")
    throw new TypeError(`${t} must be a number`);
}
function rn(t) {
  if (t.isEmpty())
    throw new Error("Empty matrix has no elements to index");
}
function mm(t) {
  let e = ii(t.rows);
  for (let n = 0; n < t.rows; ++n)
    for (let r = 0; r < t.columns; ++r)
      e[n] += t.get(n, r);
  return e;
}
function wm(t) {
  let e = ii(t.columns);
  for (let n = 0; n < t.rows; ++n)
    for (let r = 0; r < t.columns; ++r)
      e[r] += t.get(n, r);
  return e;
}
function ym(t) {
  let e = 0;
  for (let n = 0; n < t.rows; n++)
    for (let r = 0; r < t.columns; r++)
      e += t.get(n, r);
  return e;
}
function _m(t) {
  let e = ii(t.rows, 1);
  for (let n = 0; n < t.rows; ++n)
    for (let r = 0; r < t.columns; ++r)
      e[n] *= t.get(n, r);
  return e;
}
function vm(t) {
  let e = ii(t.columns, 1);
  for (let n = 0; n < t.rows; ++n)
    for (let r = 0; r < t.columns; ++r)
      e[r] *= t.get(n, r);
  return e;
}
function bm(t) {
  let e = 1;
  for (let n = 0; n < t.rows; n++)
    for (let r = 0; r < t.columns; r++)
      e *= t.get(n, r);
  return e;
}
function xm(t, e, n) {
  const r = t.rows, i = t.columns, s = [];
  for (let o = 0; o < r; o++) {
    let l = 0, u = 0, c = 0;
    for (let a = 0; a < i; a++)
      c = t.get(o, a) - n[o], l += c, u += c * c;
    e ? s.push((u - l * l / i) / (i - 1)) : s.push((u - l * l / i) / i);
  }
  return s;
}
function Em(t, e, n) {
  const r = t.rows, i = t.columns, s = [];
  for (let o = 0; o < i; o++) {
    let l = 0, u = 0, c = 0;
    for (let a = 0; a < r; a++)
      c = t.get(a, o) - n[o], l += c, u += c * c;
    e ? s.push((u - l * l / r) / (r - 1)) : s.push((u - l * l / r) / r);
  }
  return s;
}
function Sm(t, e, n) {
  const r = t.rows, i = t.columns, s = r * i;
  let o = 0, l = 0, u = 0;
  for (let c = 0; c < r; c++)
    for (let a = 0; a < i; a++)
      u = t.get(c, a) - n, o += u, l += u * u;
  return e ? (l - o * o / s) / (s - 1) : (l - o * o / s) / s;
}
function km(t, e) {
  for (let n = 0; n < t.rows; n++)
    for (let r = 0; r < t.columns; r++)
      t.set(n, r, t.get(n, r) - e[n]);
}
function Mm(t, e) {
  for (let n = 0; n < t.rows; n++)
    for (let r = 0; r < t.columns; r++)
      t.set(n, r, t.get(n, r) - e[r]);
}
function Nm(t, e) {
  for (let n = 0; n < t.rows; n++)
    for (let r = 0; r < t.columns; r++)
      t.set(n, r, t.get(n, r) - e);
}
function Rm(t) {
  const e = [];
  for (let n = 0; n < t.rows; n++) {
    let r = 0;
    for (let i = 0; i < t.columns; i++)
      r += Math.pow(t.get(n, i), 2) / (t.columns - 1);
    e.push(Math.sqrt(r));
  }
  return e;
}
function Tm(t, e) {
  for (let n = 0; n < t.rows; n++)
    for (let r = 0; r < t.columns; r++)
      t.set(n, r, t.get(n, r) / e[n]);
}
function Cm(t) {
  const e = [];
  for (let n = 0; n < t.columns; n++) {
    let r = 0;
    for (let i = 0; i < t.rows; i++)
      r += Math.pow(t.get(i, n), 2) / (t.rows - 1);
    e.push(Math.sqrt(r));
  }
  return e;
}
function Pm(t, e) {
  for (let n = 0; n < t.rows; n++)
    for (let r = 0; r < t.columns; r++)
      t.set(n, r, t.get(n, r) / e[r]);
}
function Lm(t) {
  const e = t.size - 1;
  let n = 0;
  for (let r = 0; r < t.columns; r++)
    for (let i = 0; i < t.rows; i++)
      n += Math.pow(t.get(i, r), 2) / e;
  return Math.sqrt(n);
}
function Im(t, e) {
  for (let n = 0; n < t.rows; n++)
    for (let r = 0; r < t.columns; r++)
      t.set(n, r, t.get(n, r) / e);
}
class at {
  static from1DArray(e, n, r) {
    if (e * n !== r.length)
      throw new RangeError("data length does not match given dimensions");
    let s = new st(e, n);
    for (let o = 0; o < e; o++)
      for (let l = 0; l < n; l++)
        s.set(o, l, r[o * n + l]);
    return s;
  }
  static rowVector(e) {
    let n = new st(1, e.length);
    for (let r = 0; r < e.length; r++)
      n.set(0, r, e[r]);
    return n;
  }
  static columnVector(e) {
    let n = new st(e.length, 1);
    for (let r = 0; r < e.length; r++)
      n.set(r, 0, e[r]);
    return n;
  }
  static zeros(e, n) {
    return new st(e, n);
  }
  static ones(e, n) {
    return new st(e, n).fill(1);
  }
  static rand(e, n, r = {}) {
    if (typeof r != "object")
      throw new TypeError("options must be an object");
    const { random: i = Math.random } = r;
    let s = new st(e, n);
    for (let o = 0; o < e; o++)
      for (let l = 0; l < n; l++)
        s.set(o, l, i());
    return s;
  }
  static randInt(e, n, r = {}) {
    if (typeof r != "object")
      throw new TypeError("options must be an object");
    const { min: i = 0, max: s = 1e3, random: o = Math.random } = r;
    if (!Number.isInteger(i)) throw new TypeError("min must be an integer");
    if (!Number.isInteger(s)) throw new TypeError("max must be an integer");
    if (i >= s) throw new RangeError("min must be smaller than max");
    let l = s - i, u = new st(e, n);
    for (let c = 0; c < e; c++)
      for (let a = 0; a < n; a++) {
        let f = i + Math.round(o() * l);
        u.set(c, a, f);
      }
    return u;
  }
  static eye(e, n, r) {
    n === void 0 && (n = e), r === void 0 && (r = 1);
    let i = Math.min(e, n), s = this.zeros(e, n);
    for (let o = 0; o < i; o++)
      s.set(o, o, r);
    return s;
  }
  static diag(e, n, r) {
    let i = e.length;
    n === void 0 && (n = i), r === void 0 && (r = n);
    let s = Math.min(i, n, r), o = this.zeros(n, r);
    for (let l = 0; l < s; l++)
      o.set(l, l, e[l]);
    return o;
  }
  static min(e, n) {
    e = this.checkMatrix(e), n = this.checkMatrix(n);
    let r = e.rows, i = e.columns, s = new st(r, i);
    for (let o = 0; o < r; o++)
      for (let l = 0; l < i; l++)
        s.set(o, l, Math.min(e.get(o, l), n.get(o, l)));
    return s;
  }
  static max(e, n) {
    e = this.checkMatrix(e), n = this.checkMatrix(n);
    let r = e.rows, i = e.columns, s = new this(r, i);
    for (let o = 0; o < r; o++)
      for (let l = 0; l < i; l++)
        s.set(o, l, Math.max(e.get(o, l), n.get(o, l)));
    return s;
  }
  static checkMatrix(e) {
    return at.isMatrix(e) ? e : new st(e);
  }
  static isMatrix(e) {
    return e != null && e.klass === "Matrix";
  }
  get size() {
    return this.rows * this.columns;
  }
  apply(e) {
    if (typeof e != "function")
      throw new TypeError("callback must be a function");
    for (let n = 0; n < this.rows; n++)
      for (let r = 0; r < this.columns; r++)
        e.call(this, n, r);
    return this;
  }
  to1DArray() {
    let e = [];
    for (let n = 0; n < this.rows; n++)
      for (let r = 0; r < this.columns; r++)
        e.push(this.get(n, r));
    return e;
  }
  to2DArray() {
    let e = [];
    for (let n = 0; n < this.rows; n++) {
      e.push([]);
      for (let r = 0; r < this.columns; r++)
        e[n].push(this.get(n, r));
    }
    return e;
  }
  toJSON() {
    return this.to2DArray();
  }
  isRowVector() {
    return this.rows === 1;
  }
  isColumnVector() {
    return this.columns === 1;
  }
  isVector() {
    return this.rows === 1 || this.columns === 1;
  }
  isSquare() {
    return this.rows === this.columns;
  }
  isEmpty() {
    return this.rows === 0 || this.columns === 0;
  }
  isSymmetric() {
    if (this.isSquare()) {
      for (let e = 0; e < this.rows; e++)
        for (let n = 0; n <= e; n++)
          if (this.get(e, n) !== this.get(n, e))
            return !1;
      return !0;
    }
    return !1;
  }
  isEchelonForm() {
    let e = 0, n = 0, r = -1, i = !0, s = !1;
    for (; e < this.rows && i; ) {
      for (n = 0, s = !1; n < this.columns && s === !1; )
        this.get(e, n) === 0 ? n++ : this.get(e, n) === 1 && n > r ? (s = !0, r = n) : (i = !1, s = !0);
      e++;
    }
    return i;
  }
  isReducedEchelonForm() {
    let e = 0, n = 0, r = -1, i = !0, s = !1;
    for (; e < this.rows && i; ) {
      for (n = 0, s = !1; n < this.columns && s === !1; )
        this.get(e, n) === 0 ? n++ : this.get(e, n) === 1 && n > r ? (s = !0, r = n) : (i = !1, s = !0);
      for (let o = n + 1; o < this.rows; o++)
        this.get(e, o) !== 0 && (i = !1);
      e++;
    }
    return i;
  }
  echelonForm() {
    let e = this.clone(), n = 0, r = 0;
    for (; n < e.rows && r < e.columns; ) {
      let i = n;
      for (let s = n; s < e.rows; s++)
        e.get(s, r) > e.get(i, r) && (i = s);
      if (e.get(i, r) === 0)
        r++;
      else {
        e.swapRows(n, i);
        let s = e.get(n, r);
        for (let o = r; o < e.columns; o++)
          e.set(n, o, e.get(n, o) / s);
        for (let o = n + 1; o < e.rows; o++) {
          let l = e.get(o, r) / e.get(n, r);
          e.set(o, r, 0);
          for (let u = r + 1; u < e.columns; u++)
            e.set(o, u, e.get(o, u) - e.get(n, u) * l);
        }
        n++, r++;
      }
    }
    return e;
  }
  reducedEchelonForm() {
    let e = this.echelonForm(), n = e.columns, r = e.rows, i = r - 1;
    for (; i >= 0; )
      if (e.maxRow(i) === 0)
        i--;
      else {
        let s = 0, o = !1;
        for (; s < r && o === !1; )
          e.get(i, s) === 1 ? o = !0 : s++;
        for (let l = 0; l < i; l++) {
          let u = e.get(l, s);
          for (let c = s; c < n; c++) {
            let a = e.get(l, c) - u * e.get(i, c);
            e.set(l, c, a);
          }
        }
        i--;
      }
    return e;
  }
  set() {
    throw new Error("set method is unimplemented");
  }
  get() {
    throw new Error("get method is unimplemented");
  }
  repeat(e = {}) {
    if (typeof e != "object")
      throw new TypeError("options must be an object");
    const { rows: n = 1, columns: r = 1 } = e;
    if (!Number.isInteger(n) || n <= 0)
      throw new TypeError("rows must be a positive integer");
    if (!Number.isInteger(r) || r <= 0)
      throw new TypeError("columns must be a positive integer");
    let i = new st(this.rows * n, this.columns * r);
    for (let s = 0; s < n; s++)
      for (let o = 0; o < r; o++)
        i.setSubMatrix(this, this.rows * s, this.columns * o);
    return i;
  }
  fill(e) {
    for (let n = 0; n < this.rows; n++)
      for (let r = 0; r < this.columns; r++)
        this.set(n, r, e);
    return this;
  }
  neg() {
    return this.mulS(-1);
  }
  getRow(e) {
    oe(this, e);
    let n = [];
    for (let r = 0; r < this.columns; r++)
      n.push(this.get(e, r));
    return n;
  }
  getRowVector(e) {
    return st.rowVector(this.getRow(e));
  }
  setRow(e, n) {
    oe(this, e), n = sn(this, n);
    for (let r = 0; r < this.columns; r++)
      this.set(e, r, n[r]);
    return this;
  }
  swapRows(e, n) {
    oe(this, e), oe(this, n);
    for (let r = 0; r < this.columns; r++) {
      let i = this.get(e, r);
      this.set(e, r, this.get(n, r)), this.set(n, r, i);
    }
    return this;
  }
  getColumn(e) {
    le(this, e);
    let n = [];
    for (let r = 0; r < this.rows; r++)
      n.push(this.get(r, e));
    return n;
  }
  getColumnVector(e) {
    return st.columnVector(this.getColumn(e));
  }
  setColumn(e, n) {
    le(this, e), n = on(this, n);
    for (let r = 0; r < this.rows; r++)
      this.set(r, e, n[r]);
    return this;
  }
  swapColumns(e, n) {
    le(this, e), le(this, n);
    for (let r = 0; r < this.rows; r++) {
      let i = this.get(r, e);
      this.set(r, e, this.get(r, n)), this.set(r, n, i);
    }
    return this;
  }
  addRowVector(e) {
    e = sn(this, e);
    for (let n = 0; n < this.rows; n++)
      for (let r = 0; r < this.columns; r++)
        this.set(n, r, this.get(n, r) + e[r]);
    return this;
  }
  subRowVector(e) {
    e = sn(this, e);
    for (let n = 0; n < this.rows; n++)
      for (let r = 0; r < this.columns; r++)
        this.set(n, r, this.get(n, r) - e[r]);
    return this;
  }
  mulRowVector(e) {
    e = sn(this, e);
    for (let n = 0; n < this.rows; n++)
      for (let r = 0; r < this.columns; r++)
        this.set(n, r, this.get(n, r) * e[r]);
    return this;
  }
  divRowVector(e) {
    e = sn(this, e);
    for (let n = 0; n < this.rows; n++)
      for (let r = 0; r < this.columns; r++)
        this.set(n, r, this.get(n, r) / e[r]);
    return this;
  }
  addColumnVector(e) {
    e = on(this, e);
    for (let n = 0; n < this.rows; n++)
      for (let r = 0; r < this.columns; r++)
        this.set(n, r, this.get(n, r) + e[n]);
    return this;
  }
  subColumnVector(e) {
    e = on(this, e);
    for (let n = 0; n < this.rows; n++)
      for (let r = 0; r < this.columns; r++)
        this.set(n, r, this.get(n, r) - e[n]);
    return this;
  }
  mulColumnVector(e) {
    e = on(this, e);
    for (let n = 0; n < this.rows; n++)
      for (let r = 0; r < this.columns; r++)
        this.set(n, r, this.get(n, r) * e[n]);
    return this;
  }
  divColumnVector(e) {
    e = on(this, e);
    for (let n = 0; n < this.rows; n++)
      for (let r = 0; r < this.columns; r++)
        this.set(n, r, this.get(n, r) / e[n]);
    return this;
  }
  mulRow(e, n) {
    oe(this, e);
    for (let r = 0; r < this.columns; r++)
      this.set(e, r, this.get(e, r) * n);
    return this;
  }
  mulColumn(e, n) {
    le(this, e);
    for (let r = 0; r < this.rows; r++)
      this.set(r, e, this.get(r, e) * n);
    return this;
  }
  max() {
    if (this.isEmpty())
      return NaN;
    let e = this.get(0, 0);
    for (let n = 0; n < this.rows; n++)
      for (let r = 0; r < this.columns; r++)
        this.get(n, r) > e && (e = this.get(n, r));
    return e;
  }
  maxIndex() {
    rn(this);
    let e = this.get(0, 0), n = [0, 0];
    for (let r = 0; r < this.rows; r++)
      for (let i = 0; i < this.columns; i++)
        this.get(r, i) > e && (e = this.get(r, i), n[0] = r, n[1] = i);
    return n;
  }
  min() {
    if (this.isEmpty())
      return NaN;
    let e = this.get(0, 0);
    for (let n = 0; n < this.rows; n++)
      for (let r = 0; r < this.columns; r++)
        this.get(n, r) < e && (e = this.get(n, r));
    return e;
  }
  minIndex() {
    rn(this);
    let e = this.get(0, 0), n = [0, 0];
    for (let r = 0; r < this.rows; r++)
      for (let i = 0; i < this.columns; i++)
        this.get(r, i) < e && (e = this.get(r, i), n[0] = r, n[1] = i);
    return n;
  }
  maxRow(e) {
    if (oe(this, e), this.isEmpty())
      return NaN;
    let n = this.get(e, 0);
    for (let r = 1; r < this.columns; r++)
      this.get(e, r) > n && (n = this.get(e, r));
    return n;
  }
  maxRowIndex(e) {
    oe(this, e), rn(this);
    let n = this.get(e, 0), r = [e, 0];
    for (let i = 1; i < this.columns; i++)
      this.get(e, i) > n && (n = this.get(e, i), r[1] = i);
    return r;
  }
  minRow(e) {
    if (oe(this, e), this.isEmpty())
      return NaN;
    let n = this.get(e, 0);
    for (let r = 1; r < this.columns; r++)
      this.get(e, r) < n && (n = this.get(e, r));
    return n;
  }
  minRowIndex(e) {
    oe(this, e), rn(this);
    let n = this.get(e, 0), r = [e, 0];
    for (let i = 1; i < this.columns; i++)
      this.get(e, i) < n && (n = this.get(e, i), r[1] = i);
    return r;
  }
  maxColumn(e) {
    if (le(this, e), this.isEmpty())
      return NaN;
    let n = this.get(0, e);
    for (let r = 1; r < this.rows; r++)
      this.get(r, e) > n && (n = this.get(r, e));
    return n;
  }
  maxColumnIndex(e) {
    le(this, e), rn(this);
    let n = this.get(0, e), r = [0, e];
    for (let i = 1; i < this.rows; i++)
      this.get(i, e) > n && (n = this.get(i, e), r[0] = i);
    return r;
  }
  minColumn(e) {
    if (le(this, e), this.isEmpty())
      return NaN;
    let n = this.get(0, e);
    for (let r = 1; r < this.rows; r++)
      this.get(r, e) < n && (n = this.get(r, e));
    return n;
  }
  minColumnIndex(e) {
    le(this, e), rn(this);
    let n = this.get(0, e), r = [0, e];
    for (let i = 1; i < this.rows; i++)
      this.get(i, e) < n && (n = this.get(i, e), r[0] = i);
    return r;
  }
  diag() {
    let e = Math.min(this.rows, this.columns), n = [];
    for (let r = 0; r < e; r++)
      n.push(this.get(r, r));
    return n;
  }
  norm(e = "frobenius") {
    let n = 0;
    if (e === "max")
      return this.max();
    if (e === "frobenius") {
      for (let r = 0; r < this.rows; r++)
        for (let i = 0; i < this.columns; i++)
          n = n + this.get(r, i) * this.get(r, i);
      return Math.sqrt(n);
    } else
      throw new RangeError(`unknown norm type: ${e}`);
  }
  cumulativeSum() {
    let e = 0;
    for (let n = 0; n < this.rows; n++)
      for (let r = 0; r < this.columns; r++)
        e += this.get(n, r), this.set(n, r, e);
    return this;
  }
  dot(e) {
    at.isMatrix(e) && (e = e.to1DArray());
    let n = this.to1DArray();
    if (n.length !== e.length)
      throw new RangeError("vectors do not have the same size");
    let r = 0;
    for (let i = 0; i < n.length; i++)
      r += n[i] * e[i];
    return r;
  }
  mmul(e) {
    e = st.checkMatrix(e);
    let n = this.rows, r = this.columns, i = e.columns, s = new st(n, i), o = new Float64Array(r);
    for (let l = 0; l < i; l++) {
      for (let u = 0; u < r; u++)
        o[u] = e.get(u, l);
      for (let u = 0; u < n; u++) {
        let c = 0;
        for (let a = 0; a < r; a++)
          c += this.get(u, a) * o[a];
        s.set(u, l, c);
      }
    }
    return s;
  }
  strassen2x2(e) {
    e = st.checkMatrix(e);
    let n = new st(2, 2);
    const r = this.get(0, 0), i = e.get(0, 0), s = this.get(0, 1), o = e.get(0, 1), l = this.get(1, 0), u = e.get(1, 0), c = this.get(1, 1), a = e.get(1, 1), f = (r + c) * (i + a), h = (l + c) * i, p = r * (o - a), w = c * (u - i), _ = (r + s) * a, v = (l - r) * (i + o), d = (s - c) * (u + a), T = f + w - _ + d, P = p + _, y = h + w, k = f - h + p + v;
    return n.set(0, 0, T), n.set(0, 1, P), n.set(1, 0, y), n.set(1, 1, k), n;
  }
  strassen3x3(e) {
    e = st.checkMatrix(e);
    let n = new st(3, 3);
    const r = this.get(0, 0), i = this.get(0, 1), s = this.get(0, 2), o = this.get(1, 0), l = this.get(1, 1), u = this.get(1, 2), c = this.get(2, 0), a = this.get(2, 1), f = this.get(2, 2), h = e.get(0, 0), p = e.get(0, 1), w = e.get(0, 2), _ = e.get(1, 0), v = e.get(1, 1), d = e.get(1, 2), T = e.get(2, 0), P = e.get(2, 1), y = e.get(2, 2), k = (r + i + s - o - l - a - f) * v, L = (r - o) * (-p + v), z = l * (-h + p + _ - v - d - T + y), G = (-r + o + l) * (h - p + v), Q = (o + l) * (-h + p), X = r * h, et = (-r + c + a) * (h - w + d), ot = (-r + c) * (w - d), U = (c + a) * (-h + w), x = (r + i + s - l - u - c - a) * d, D = a * (-h + w + _ - v - d - T + P), N = (-s + a + f) * (v + T - P), j = (s - f) * (v - P), B = s * T, Y = (a + f) * (-T + P), J = (-s + l + u) * (d + T - y), it = (s - u) * (d - y), lt = (l + u) * (-T + y), kt = i * _, pt = u * P, gt = o * w, mt = c * p, xt = f * y, Mt = X + B + kt, Dt = k + G + Q + X + N + B + Y, Zt = X + et + U + x + B + J + lt, de = L + z + G + X + B + J + it, De = L + G + Q + X + pt, m = B + J + it + lt + gt, b = X + et + ot + D + N + j + B, C = N + j + B + Y + mt, $ = X + et + ot + U + xt;
    return n.set(0, 0, Mt), n.set(0, 1, Dt), n.set(0, 2, Zt), n.set(1, 0, de), n.set(1, 1, De), n.set(1, 2, m), n.set(2, 0, b), n.set(2, 1, C), n.set(2, 2, $), n;
  }
  mmulStrassen(e) {
    e = st.checkMatrix(e);
    let n = this.clone(), r = n.rows, i = n.columns, s = e.rows, o = e.columns;
    i !== s && console.warn(
      `Multiplying ${r} x ${i} and ${s} x ${o} matrix: dimensions do not match.`
    );
    function l(f, h, p) {
      let w = f.rows, _ = f.columns;
      if (w === h && _ === p)
        return f;
      {
        let v = at.zeros(h, p);
        return v = v.setSubMatrix(f, 0, 0), v;
      }
    }
    let u = Math.max(r, s), c = Math.max(i, o);
    n = l(n, u, c), e = l(e, u, c);
    function a(f, h, p, w) {
      if (p <= 512 || w <= 512)
        return f.mmul(h);
      p % 2 === 1 && w % 2 === 1 ? (f = l(f, p + 1, w + 1), h = l(h, p + 1, w + 1)) : p % 2 === 1 ? (f = l(f, p + 1, w), h = l(h, p + 1, w)) : w % 2 === 1 && (f = l(f, p, w + 1), h = l(h, p, w + 1));
      let _ = parseInt(f.rows / 2, 10), v = parseInt(f.columns / 2, 10), d = f.subMatrix(0, _ - 1, 0, v - 1), T = h.subMatrix(0, _ - 1, 0, v - 1), P = f.subMatrix(0, _ - 1, v, f.columns - 1), y = h.subMatrix(0, _ - 1, v, h.columns - 1), k = f.subMatrix(_, f.rows - 1, 0, v - 1), L = h.subMatrix(_, h.rows - 1, 0, v - 1), z = f.subMatrix(_, f.rows - 1, v, f.columns - 1), G = h.subMatrix(_, h.rows - 1, v, h.columns - 1), Q = a(
        at.add(d, z),
        at.add(T, G),
        _,
        v
      ), X = a(at.add(k, z), T, _, v), et = a(d, at.sub(y, G), _, v), ot = a(z, at.sub(L, T), _, v), U = a(at.add(d, P), G, _, v), x = a(
        at.sub(k, d),
        at.add(T, y),
        _,
        v
      ), D = a(
        at.sub(P, z),
        at.add(L, G),
        _,
        v
      ), N = at.add(Q, ot);
      N.sub(U), N.add(D);
      let j = at.add(et, U), B = at.add(X, ot), Y = at.sub(Q, X);
      Y.add(et), Y.add(x);
      let J = at.zeros(2 * N.rows, 2 * N.columns);
      return J = J.setSubMatrix(N, 0, 0), J = J.setSubMatrix(j, N.rows, 0), J = J.setSubMatrix(B, 0, N.columns), J = J.setSubMatrix(Y, N.rows, N.columns), J.subMatrix(0, p - 1, 0, w - 1);
    }
    return a(n, e, u, c);
  }
  scaleRows(e = {}) {
    if (typeof e != "object")
      throw new TypeError("options must be an object");
    const { min: n = 0, max: r = 1 } = e;
    if (!Number.isFinite(n)) throw new TypeError("min must be a number");
    if (!Number.isFinite(r)) throw new TypeError("max must be a number");
    if (n >= r) throw new RangeError("min must be smaller than max");
    let i = new st(this.rows, this.columns);
    for (let s = 0; s < this.rows; s++) {
      const o = this.getRow(s);
      o.length > 0 && Bo(o, { min: n, max: r, output: o }), i.setRow(s, o);
    }
    return i;
  }
  scaleColumns(e = {}) {
    if (typeof e != "object")
      throw new TypeError("options must be an object");
    const { min: n = 0, max: r = 1 } = e;
    if (!Number.isFinite(n)) throw new TypeError("min must be a number");
    if (!Number.isFinite(r)) throw new TypeError("max must be a number");
    if (n >= r) throw new RangeError("min must be smaller than max");
    let i = new st(this.rows, this.columns);
    for (let s = 0; s < this.columns; s++) {
      const o = this.getColumn(s);
      o.length && Bo(o, {
        min: n,
        max: r,
        output: o
      }), i.setColumn(s, o);
    }
    return i;
  }
  flipRows() {
    const e = Math.ceil(this.columns / 2);
    for (let n = 0; n < this.rows; n++)
      for (let r = 0; r < e; r++) {
        let i = this.get(n, r), s = this.get(n, this.columns - 1 - r);
        this.set(n, r, s), this.set(n, this.columns - 1 - r, i);
      }
    return this;
  }
  flipColumns() {
    const e = Math.ceil(this.rows / 2);
    for (let n = 0; n < this.columns; n++)
      for (let r = 0; r < e; r++) {
        let i = this.get(r, n), s = this.get(this.rows - 1 - r, n);
        this.set(r, n, s), this.set(this.rows - 1 - r, n, i);
      }
    return this;
  }
  kroneckerProduct(e) {
    e = st.checkMatrix(e);
    let n = this.rows, r = this.columns, i = e.rows, s = e.columns, o = new st(n * i, r * s);
    for (let l = 0; l < n; l++)
      for (let u = 0; u < r; u++)
        for (let c = 0; c < i; c++)
          for (let a = 0; a < s; a++)
            o.set(i * l + c, s * u + a, this.get(l, u) * e.get(c, a));
    return o;
  }
  kroneckerSum(e) {
    if (e = st.checkMatrix(e), !this.isSquare() || !e.isSquare())
      throw new Error("Kronecker Sum needs two Square Matrices");
    let n = this.rows, r = e.rows, i = this.kroneckerProduct(st.eye(r, r)), s = st.eye(n, n).kroneckerProduct(e);
    return i.add(s);
  }
  transpose() {
    let e = new st(this.columns, this.rows);
    for (let n = 0; n < this.rows; n++)
      for (let r = 0; r < this.columns; r++)
        e.set(r, n, this.get(n, r));
    return e;
  }
  sortRows(e = Do) {
    for (let n = 0; n < this.rows; n++)
      this.setRow(n, this.getRow(n).sort(e));
    return this;
  }
  sortColumns(e = Do) {
    for (let n = 0; n < this.columns; n++)
      this.setColumn(n, this.getColumn(n).sort(e));
    return this;
  }
  subMatrix(e, n, r, i) {
    zo(this, e, n, r, i);
    let s = new st(
      n - e + 1,
      i - r + 1
    );
    for (let o = e; o <= n; o++)
      for (let l = r; l <= i; l++)
        s.set(o - e, l - r, this.get(o, l));
    return s;
  }
  subMatrixRow(e, n, r) {
    if (n === void 0 && (n = 0), r === void 0 && (r = this.columns - 1), n > r || n < 0 || n >= this.columns || r < 0 || r >= this.columns)
      throw new RangeError("Argument out of range");
    let i = new st(e.length, r - n + 1);
    for (let s = 0; s < e.length; s++)
      for (let o = n; o <= r; o++) {
        if (e[s] < 0 || e[s] >= this.rows)
          throw new RangeError(`Row index out of range: ${e[s]}`);
        i.set(s, o - n, this.get(e[s], o));
      }
    return i;
  }
  subMatrixColumn(e, n, r) {
    if (n === void 0 && (n = 0), r === void 0 && (r = this.rows - 1), n > r || n < 0 || n >= this.rows || r < 0 || r >= this.rows)
      throw new RangeError("Argument out of range");
    let i = new st(r - n + 1, e.length);
    for (let s = 0; s < e.length; s++)
      for (let o = n; o <= r; o++) {
        if (e[s] < 0 || e[s] >= this.columns)
          throw new RangeError(`Column index out of range: ${e[s]}`);
        i.set(o - n, s, this.get(o, e[s]));
      }
    return i;
  }
  setSubMatrix(e, n, r) {
    if (e = st.checkMatrix(e), e.isEmpty())
      return this;
    let i = n + e.rows - 1, s = r + e.columns - 1;
    zo(this, n, i, r, s);
    for (let o = 0; o < e.rows; o++)
      for (let l = 0; l < e.columns; l++)
        this.set(n + o, r + l, e.get(o, l));
    return this;
  }
  selection(e, n) {
    let r = dm(this, e, n), i = new st(e.length, n.length);
    for (let s = 0; s < r.row.length; s++) {
      let o = r.row[s];
      for (let l = 0; l < r.column.length; l++) {
        let u = r.column[l];
        i.set(s, l, this.get(o, u));
      }
    }
    return i;
  }
  trace() {
    let e = Math.min(this.rows, this.columns), n = 0;
    for (let r = 0; r < e; r++)
      n += this.get(r, r);
    return n;
  }
  clone() {
    let e = new st(this.rows, this.columns);
    for (let n = 0; n < this.rows; n++)
      for (let r = 0; r < this.columns; r++)
        e.set(n, r, this.get(n, r));
    return e;
  }
  sum(e) {
    switch (e) {
      case "row":
        return mm(this);
      case "column":
        return wm(this);
      case void 0:
        return ym(this);
      default:
        throw new Error(`invalid option: ${e}`);
    }
  }
  product(e) {
    switch (e) {
      case "row":
        return _m(this);
      case "column":
        return vm(this);
      case void 0:
        return bm(this);
      default:
        throw new Error(`invalid option: ${e}`);
    }
  }
  mean(e) {
    const n = this.sum(e);
    switch (e) {
      case "row": {
        for (let r = 0; r < this.rows; r++)
          n[r] /= this.columns;
        return n;
      }
      case "column": {
        for (let r = 0; r < this.columns; r++)
          n[r] /= this.rows;
        return n;
      }
      case void 0:
        return n / this.size;
      default:
        throw new Error(`invalid option: ${e}`);
    }
  }
  variance(e, n = {}) {
    if (typeof e == "object" && (n = e, e = void 0), typeof n != "object")
      throw new TypeError("options must be an object");
    const { unbiased: r = !0, mean: i = this.mean(e) } = n;
    if (typeof r != "boolean")
      throw new TypeError("unbiased must be a boolean");
    switch (e) {
      case "row": {
        if (!Array.isArray(i))
          throw new TypeError("mean must be an array");
        return xm(this, r, i);
      }
      case "column": {
        if (!Array.isArray(i))
          throw new TypeError("mean must be an array");
        return Em(this, r, i);
      }
      case void 0: {
        if (typeof i != "number")
          throw new TypeError("mean must be a number");
        return Sm(this, r, i);
      }
      default:
        throw new Error(`invalid option: ${e}`);
    }
  }
  standardDeviation(e, n) {
    typeof e == "object" && (n = e, e = void 0);
    const r = this.variance(e, n);
    if (e === void 0)
      return Math.sqrt(r);
    for (let i = 0; i < r.length; i++)
      r[i] = Math.sqrt(r[i]);
    return r;
  }
  center(e, n = {}) {
    if (typeof e == "object" && (n = e, e = void 0), typeof n != "object")
      throw new TypeError("options must be an object");
    const { center: r = this.mean(e) } = n;
    switch (e) {
      case "row": {
        if (!Array.isArray(r))
          throw new TypeError("center must be an array");
        return km(this, r), this;
      }
      case "column": {
        if (!Array.isArray(r))
          throw new TypeError("center must be an array");
        return Mm(this, r), this;
      }
      case void 0: {
        if (typeof r != "number")
          throw new TypeError("center must be a number");
        return Nm(this, r), this;
      }
      default:
        throw new Error(`invalid option: ${e}`);
    }
  }
  scale(e, n = {}) {
    if (typeof e == "object" && (n = e, e = void 0), typeof n != "object")
      throw new TypeError("options must be an object");
    let r = n.scale;
    switch (e) {
      case "row": {
        if (r === void 0)
          r = Rm(this);
        else if (!Array.isArray(r))
          throw new TypeError("scale must be an array");
        return Tm(this, r), this;
      }
      case "column": {
        if (r === void 0)
          r = Cm(this);
        else if (!Array.isArray(r))
          throw new TypeError("scale must be an array");
        return Pm(this, r), this;
      }
      case void 0: {
        if (r === void 0)
          r = Lm(this);
        else if (typeof r != "number")
          throw new TypeError("scale must be a number");
        return Im(this, r), this;
      }
      default:
        throw new Error(`invalid option: ${e}`);
    }
  }
  toString(e) {
    return ku(this, e);
  }
}
at.prototype.klass = "Matrix";
typeof Symbol < "u" && (at.prototype[Symbol.for("nodejs.util.inspect.custom")] = cm);
function Do(t, e) {
  return t - e;
}
at.random = at.rand;
at.randomInt = at.randInt;
at.diagonal = at.diag;
at.prototype.diagonal = at.prototype.diag;
at.identity = at.eye;
at.prototype.negate = at.prototype.neg;
at.prototype.tensorProduct = at.prototype.kroneckerProduct;
class st extends at {
  constructor(e, n) {
    if (super(), st.isMatrix(e))
      return e.clone();
    if (Number.isInteger(e) && e >= 0)
      if (this.data = [], Number.isInteger(n) && n >= 0)
        for (let r = 0; r < e; r++)
          this.data.push(new Float64Array(n));
      else
        throw new TypeError("nColumns must be a positive integer");
    else if (Array.isArray(e)) {
      const r = e;
      if (e = r.length, n = e ? r[0].length : 0, typeof n != "number")
        throw new TypeError(
          "Data must be a 2D array with at least one element"
        );
      this.data = [];
      for (let i = 0; i < e; i++) {
        if (r[i].length !== n)
          throw new RangeError("Inconsistent array dimensions");
        this.data.push(Float64Array.from(r[i]));
      }
    } else
      throw new TypeError(
        "First argument must be a positive number or an array"
      );
    this.rows = e, this.columns = n;
  }
  set(e, n, r) {
    return this.data[e][n] = r, this;
  }
  get(e, n) {
    return this.data[e][n];
  }
  removeRow(e) {
    return oe(this, e), this.data.splice(e, 1), this.rows -= 1, this;
  }
  addRow(e, n) {
    return n === void 0 && (n = e, e = this.rows), oe(this, e, !0), n = Float64Array.from(sn(this, n)), this.data.splice(e, 0, n), this.rows += 1, this;
  }
  removeColumn(e) {
    le(this, e);
    for (let n = 0; n < this.rows; n++) {
      const r = new Float64Array(this.columns - 1);
      for (let i = 0; i < e; i++)
        r[i] = this.data[n][i];
      for (let i = e + 1; i < this.columns; i++)
        r[i - 1] = this.data[n][i];
      this.data[n] = r;
    }
    return this.columns -= 1, this;
  }
  addColumn(e, n) {
    typeof n > "u" && (n = e, e = this.columns), le(this, e, !0), n = on(this, n);
    for (let r = 0; r < this.rows; r++) {
      const i = new Float64Array(this.columns + 1);
      let s = 0;
      for (; s < e; s++)
        i[s] = this.data[r][s];
      for (i[s++] = n[r]; s < this.columns + 1; s++)
        i[s] = this.data[r][s - 1];
      this.data[r] = i;
    }
    return this.columns += 1, this;
  }
}
hm(at, st);
function Si(t, e, n) {
  const r = e.x - t.x, i = e.y - t.y, s = Math.sqrt(r * r + i * i), o = r / s, l = i / s, u = t.x + (n.nodeRadius - 1) * o, c = t.y + (n.nodeRadius - 1) * l, a = e.x - n.markerPadding * o, f = e.y - n.markerPadding * l;
  return `M${u},${c}
          L${a},${f}`;
}
function ki(t, e, n) {
  const r = new st([[t.x, t.y]]), i = new st([[e.x, e.y]]), s = st.subtract(i, r), o = s.norm("frobenius"), l = s.divide(o), u = Mu(10), c = pn(l, -u).multiply(n.nodeRadius - 1).add(r), a = st.multiply(l, -1), f = pn(a, u).multiply(n.nodeRadius).add(i).add(pn(a, u).multiply(2 * n.markerBoxSize)), h = 1.2 * o;
  return `M${c.get(0, 0)},${c.get(0, 1)}
          A${h},${h},0,0,1,${f.get(0, 0)},${f.get(0, 1)}`;
}
function Vo(t, e, n) {
  const r = new st([[t.x, t.y]]), i = new st([e]);
  r.get(0, 0) === i.get(0, 0) && r.get(0, 1) === i.get(0, 1) && i.add([[0, 1]]);
  const s = st.subtract(r, i), o = s.divide(s.norm("frobenius")), l = Mu(40), u = pn(o, l).multiply(n.nodeRadius - 1).add(r), c = pn(o, -l).multiply(n.nodeRadius).add(r).add(pn(o, -l).multiply(2 * n.markerBoxSize));
  return `M${u.get(0, 0)},${u.get(0, 1)}
          A${n.nodeRadius},${n.nodeRadius},0,1,0,${c.get(0, 0)},${c.get(0, 1)}`;
}
function qo(t, e) {
  return `M${t[0]},${t[1]}
          L${e[0]},${e[1]}`;
}
function Mu(t) {
  return t * (Math.PI / 180);
}
function pn(t, e) {
  const n = t.get(0, 0), r = t.get(0, 1);
  return new st([
    [
      n * Math.cos(e) - r * Math.sin(e),
      n * Math.sin(e) + r * Math.cos(e)
    ]
  ]);
}
class $m {
  constructor() {
    yt(this, "persistSettingsLocalStorage", !1);
    yt(this, "hasToolbar", !1);
    yt(this, "_nodeRadius", 24);
    yt(this, "showNodeLabels", !0);
    yt(this, "nodePhysicsEnabled", !1);
    yt(this, "isGraphEditableInGUI", !0);
    yt(this, "zoomEnabled", !1);
    yt(this, "showLinkLabels", !0);
    yt(this, "fixedLinkDistanceEnabled", !1);
    yt(this, "markerBoxSize", 4);
    yt(this, "_markerPadding", this._nodeRadius + 2 * this.markerBoxSize);
  }
  get nodeRadius() {
    return this._nodeRadius;
  }
  set nodeRadius(e) {
    this._nodeRadius = e, this._markerPadding = this._nodeRadius + 2 * this.markerBoxSize;
  }
  get markerPadding() {
    return this._markerPadding;
  }
  get markerRef() {
    return this.markerBoxSize / 2;
  }
  get arrowPoints() {
    return [
      [0, 0],
      [0, this.markerBoxSize],
      [this.markerBoxSize, this.markerBoxSize / 2]
    ];
  }
  get markerPath() {
    return [0, 0, this.markerBoxSize, this.markerBoxSize].join(",");
  }
}
function Om(t) {
  const e = t.replace(/\r\n/g, `
`).split(`
`), n = e.findIndex((l) => l.trim().startsWith("#")), r = n !== -1 ? e.slice(0, n) : e, i = n !== -1 ? e.slice(n + 1) : [], s = [];
  if (r.length)
    for (const l of r) {
      let [, u, c, a] = (l.match(/(\w+) (.*) \/COLOR:\/(.+)/) || l.match(/(\w+) (.*)/) || l.match(/(\w+)/) || []).map((f) => f.trim());
      c != null && c.includes("/COLOR:/") && (a = c, c = ""), u && s.push({
        idImported: u,
        label: c,
        color: a == null ? void 0 : a.replace("/COLOR:/", "")
      });
    }
  const o = [];
  if (i.length)
    for (const l of i) {
      let [, u, c, a, f] = (l.match(/(\w+) (\w+) (.*) \/COLOR:\/(.+)/) || l.match(/(\w+) (\w+) (.*)/) || l.match(/(\w+) (\w+)/) || []).map((h) => h.trim());
      a != null && a.includes("/COLOR:/") && (f = a, a = ""), u && c && o.push({
        sourceIdImported: u,
        targetIdImported: c,
        label: a,
        color: f == null ? void 0 : f.replace("/COLOR:/", "")
      });
    }
  return [s, o];
}
function Am(t) {
  const e = [];
  for (let r of t.nodes)
    e.push({
      idImported: r.id,
      x: r.x,
      y: r.y,
      label: r.label,
      color: r.color,
      fixedPosition: r.fixedPosition,
      deletable: r.deletable,
      labelEditable: r.labelEditable,
      allowIncomingLinks: r.allowIncomingLinks,
      allowOutgoingLinks: r.allowOutgoingLinks
    });
  const n = [];
  for (let r of t.links)
    n.push({
      sourceIdImported: r.sourceId,
      targetIdImported: r.targetId,
      label: r.label,
      color: r.color,
      deletable: r.deletable,
      labelEditable: r.labelEditable
    });
  return [e, n];
}
var Fm = typeof globalThis < "u" ? globalThis : typeof window < "u" ? window : typeof global < "u" ? global : typeof self < "u" ? self : {};
function jm(t) {
  return t && t.__esModule && Object.prototype.hasOwnProperty.call(t, "default") ? t.default : t;
}
var Nu = { exports: {} };
(function(t, e) {
  (function(n, r) {
    t.exports = r();
  })(Fm, function() {
    function n(o) {
      o = o.replace(/,/g, " ").replace(/([^eE])-/g, "$1 -").replace(/\s*([achlmqstvzACHLMQSTVZ])\s*/g, " $1 ").replace(/\s+/g, " ").replace(/(\d*\.\d+([eE]-?\d+)?)((\.\d+)+)/g, (N, j, B, Y) => j + Y.replaceAll(".", " ."));
      var l = o.replace(/([achlmqstvzACHLMQSTVZ])\s?/g, "|$1").split("|"), u = l.length, c, a, f, h, p, w = [], _ = [], v, d, T = 0, P = 0, y = 0, k = 0, L = 0, z = 0, G = 0, Q = 0, X = 0, et = 0, ot = 0, U = 0, x = 0, D = "";
      for (c = 1; c < u; c++) {
        if (a = l[c], f = a.substring(0, 1), h = f.toLowerCase(), w = a.replace(f, "").trim().split(" ").filter(function(N) {
          return N !== "";
        }), _ = w, w = w.map(parseFloat), v = w.length, h === "m") {
          if (D += "M ", f === "m" ? (y += w[0], k += w[1]) : (y = w[0], k = w[1]), T = y, P = k, D += y + " " + k + " ", v > 2)
            for (d = 0; d < v; d += 2)
              f === "m" ? (y += w[d], k += w[d + 1]) : (y = w[d], k = w[d + 1]), D += "L " + y + " " + k + " ";
        } else if (h === "l")
          for (d = 0; d < v; d += 2)
            f === "l" ? (y += w[d], k += w[d + 1]) : (y = w[d], k = w[d + 1]), D += "L " + y + " " + k + " ";
        else if (h === "h")
          for (d = 0; d < v; d++)
            f === "h" ? y += w[d] : y = w[d], D += "L " + y + " " + k + " ";
        else if (h === "v")
          for (d = 0; d < v; d++)
            f === "v" ? k += w[d] : k = w[d], D += "L " + y + " " + k + " ";
        else if (h === "q")
          for (d = 0; d < v; d += 4)
            f === "q" ? (L = y + w[d], z = k + w[d + 1], y += w[d + 2], k += w[d + 3]) : (L = w[d], z = w[d + 1], y = w[d + 2], k = w[d + 3]), D += "Q " + L + " " + z + " " + y + " " + k + " ";
        else if (h === "t")
          for (d = 0; d < v; d += 2)
            ["t", "q"].indexOf(p) > -1 ? (L = y + (y - L), z = k + (k - z)) : (L = y, z = k), f === "t" ? (y += w[d], k += w[d + 1]) : (y = w[d], k = w[d + 1]), D += "Q " + L + " " + z + " " + y + " " + k + " ", p = h;
        else if (h === "c")
          for (d = 0; d < v; d += 6)
            f === "c" ? (L = y + w[d], z = k + w[d + 1], G = y + w[d + 2], Q = k + w[d + 3], y += w[d + 4], k += w[d + 5]) : (L = w[d], z = w[d + 1], G = w[d + 2], Q = w[d + 3], y = w[d + 4], k = w[d + 5]), D += "C " + L + " " + z + " " + G + " " + Q + " " + y + " " + k + " ";
        else if (h === "s")
          for (d = 0; d < v; d += 4)
            L = y, z = k, ["s", "c"].indexOf(p) > -1 && (L += y - G, z += k - Q), f === "s" ? (G = y + w[d], Q = k + w[d + 1], y += w[d + 2], k += w[d + 3]) : (G = w[d], Q = w[d + 1], y = w[d + 2], k = w[d + 3]), D += "C " + L + " " + z + " " + G + " " + Q + " " + y + " " + k + " ";
        else if (h === "a")
          for (d = 0; d < v; d += 7) {
            X = w[d], et = w[d + 1], ot = w[d + 2], U = _[d + 3];
            let N = !1;
            if (U.length > 1) {
              let j = parseInt(U[0]), B = parseInt(U[1]), Y;
              U.length > 2 && (Y = parseFloat(U.substring(2))), w[d + 3] = j, w.splice(d + 4, 0, B), _.splice(d + 4, 0, "+"), Y !== void 0 && w.splice(d + 5, 0, Y), N = !0;
            }
            U = w[d + 3], x = N ? w[d + 4] : _[d + 4], !N && x.length > 1 && (w[d + 4] = parseInt(x[0]), w.splice(d + 5, 0, parseFloat(x.substring(1)))), x = w[d + 4], f === "a" ? (y += w[d + 5], k += w[d + 6]) : (y = w[d + 5], k = w[d + 6]), D += "A " + X + " " + et + " " + ot + " " + U + " " + x + " " + y + " " + k + " ";
          }
        else h === "z" && (D += "Z ", y = T, k = P);
        p = h;
      }
      return D.trim();
    }
    function r(o) {
      var l = o.trim().split(" "), u, c = l.length, a = c - 1, f, h = [], p, w, _, v, d, T = new RegExp("[QAZLCM]", ""), P = l.slice(-1)[0].toUpperCase() === "Z";
      for (f = 0; f < c; f++)
        if (u = l[f], T.test(u)) {
          if (u === "A") {
            h.push(l[f + 5] === "0" ? "1" : "0"), h.push(l[f + 4]), h.push(l[f + 3]), h.push(l[f + 2]), h.push(l[f + 1]), h.push(u), h.push(l[f + 7]), h.push(l[f + 6]), f += 7;
            continue;
          } else if (u === "C")
            v = 3, d = 2;
          else if (u === "Q")
            v = 2, d = 1;
          else if (u === "L")
            v = 1, d = 1;
          else if (u === "M")
            v = 1, d = 0;
          else
            continue;
          for (v === d && h.push(u), _ = 0; _ < v; _++)
            _ === d && h.push(u), p = l[++f], w = l[++f], h.push(w), h.push(p);
        } else {
          var y = l.slice(Math.max(f - 3, 0), 3).join(" ");
          throw post = l.slice(f + 1, Math.min(f + 4, a)).join(" "), range = y + " [" + u + "] " + post, "Error while trying to reverse normalized SVG path, at position " + f + " (" + range + `).
Either the path is not normalised, or it's malformed.`;
        }
      h.push("M");
      var k = "", L = h.length - 1, z;
      for (z = L; z > 0; z--)
        k += h[z] + " ";
      return P && (k += "Z"), k = k.replace(/M M/g, "Z M"), k;
    }
    function i(u, l) {
      l = parseInt(l) == l ? l : !1;
      var u = n(u), c = u.replace(/M/g, "|M").split("|"), a;
      if (c.splice(0, 1), l !== !1 && l >= c.length)
        return u;
      if (l === !1)
        c = c.map(function(h) {
          return r(h.trim());
        });
      else {
        var f = c[l];
        f && (a = r(f.trim()), c[l] = a);
      }
      return c.reverse().join(" ").replace(/ +/g, " ").trim();
    }
    var s = {
      normalize: n,
      reverseNormalized: r,
      reverse: i
    };
    return s;
  });
})(Nu);
var Bm = Nu.exports;
const Go = /* @__PURE__ */ jm(Bm), zm = {
  "Amazon Silk": "amazon_silk",
  "Android Browser": "android",
  Bada: "bada",
  BlackBerry: "blackberry",
  Chrome: "chrome",
  Chromium: "chromium",
  Electron: "electron",
  Epiphany: "epiphany",
  Firefox: "firefox",
  Focus: "focus",
  Generic: "generic",
  "Google Search": "google_search",
  Googlebot: "googlebot",
  "Internet Explorer": "ie",
  "K-Meleon": "k_meleon",
  Maxthon: "maxthon",
  "Microsoft Edge": "edge",
  "MZ Browser": "mz",
  "NAVER Whale Browser": "naver",
  Opera: "opera",
  "Opera Coast": "opera_coast",
  PhantomJS: "phantomjs",
  Puffin: "puffin",
  QupZilla: "qupzilla",
  QQ: "qq",
  QQLite: "qqlite",
  Safari: "safari",
  Sailfish: "sailfish",
  "Samsung Internet for Android": "samsung_internet",
  SeaMonkey: "seamonkey",
  Sleipnir: "sleipnir",
  Swing: "swing",
  Tizen: "tizen",
  "UC Browser": "uc",
  Vivaldi: "vivaldi",
  "WebOS Browser": "webos",
  WeChat: "wechat",
  "Yandex Browser": "yandex",
  Roku: "roku"
}, Ru = {
  amazon_silk: "Amazon Silk",
  android: "Android Browser",
  bada: "Bada",
  blackberry: "BlackBerry",
  chrome: "Chrome",
  chromium: "Chromium",
  electron: "Electron",
  epiphany: "Epiphany",
  firefox: "Firefox",
  focus: "Focus",
  generic: "Generic",
  googlebot: "Googlebot",
  google_search: "Google Search",
  ie: "Internet Explorer",
  k_meleon: "K-Meleon",
  maxthon: "Maxthon",
  edge: "Microsoft Edge",
  mz: "MZ Browser",
  naver: "NAVER Whale Browser",
  opera: "Opera",
  opera_coast: "Opera Coast",
  phantomjs: "PhantomJS",
  puffin: "Puffin",
  qupzilla: "QupZilla",
  qq: "QQ Browser",
  qqlite: "QQ Browser Lite",
  safari: "Safari",
  sailfish: "Sailfish",
  samsung_internet: "Samsung Internet for Android",
  seamonkey: "SeaMonkey",
  sleipnir: "Sleipnir",
  swing: "Swing",
  tizen: "Tizen",
  uc: "UC Browser",
  vivaldi: "Vivaldi",
  webos: "WebOS Browser",
  wechat: "WeChat",
  yandex: "Yandex Browser"
}, Nt = {
  tablet: "tablet",
  mobile: "mobile",
  desktop: "desktop",
  tv: "tv"
}, At = {
  WindowsPhone: "Windows Phone",
  Windows: "Windows",
  MacOS: "macOS",
  iOS: "iOS",
  Android: "Android",
  WebOS: "WebOS",
  BlackBerry: "BlackBerry",
  Bada: "Bada",
  Tizen: "Tizen",
  Linux: "Linux",
  ChromeOS: "Chrome OS",
  PlayStation4: "PlayStation 4",
  Roku: "Roku"
}, Ie = {
  EdgeHTML: "EdgeHTML",
  Blink: "Blink",
  Trident: "Trident",
  Presto: "Presto",
  Gecko: "Gecko",
  WebKit: "WebKit"
};
class I {
  /**
   * Get first matched item for a string
   * @param {RegExp} regexp
   * @param {String} ua
   * @return {Array|{index: number, input: string}|*|boolean|string}
   */
  static getFirstMatch(e, n) {
    const r = n.match(e);
    return r && r.length > 0 && r[1] || "";
  }
  /**
   * Get second matched item for a string
   * @param regexp
   * @param {String} ua
   * @return {Array|{index: number, input: string}|*|boolean|string}
   */
  static getSecondMatch(e, n) {
    const r = n.match(e);
    return r && r.length > 1 && r[2] || "";
  }
  /**
   * Match a regexp and return a constant or undefined
   * @param {RegExp} regexp
   * @param {String} ua
   * @param {*} _const Any const that will be returned if regexp matches the string
   * @return {*}
   */
  static matchAndReturnConst(e, n, r) {
    if (e.test(n))
      return r;
  }
  static getWindowsVersionName(e) {
    switch (e) {
      case "NT":
        return "NT";
      case "XP":
        return "XP";
      case "NT 5.0":
        return "2000";
      case "NT 5.1":
        return "XP";
      case "NT 5.2":
        return "2003";
      case "NT 6.0":
        return "Vista";
      case "NT 6.1":
        return "7";
      case "NT 6.2":
        return "8";
      case "NT 6.3":
        return "8.1";
      case "NT 10.0":
        return "10";
      default:
        return;
    }
  }
  /**
   * Get macOS version name
   *    10.5 - Leopard
   *    10.6 - Snow Leopard
   *    10.7 - Lion
   *    10.8 - Mountain Lion
   *    10.9 - Mavericks
   *    10.10 - Yosemite
   *    10.11 - El Capitan
   *    10.12 - Sierra
   *    10.13 - High Sierra
   *    10.14 - Mojave
   *    10.15 - Catalina
   *
   * @example
   *   getMacOSVersionName("10.14") // 'Mojave'
   *
   * @param  {string} version
   * @return {string} versionName
   */
  static getMacOSVersionName(e) {
    const n = e.split(".").splice(0, 2).map((r) => parseInt(r, 10) || 0);
    if (n.push(0), n[0] === 10)
      switch (n[1]) {
        case 5:
          return "Leopard";
        case 6:
          return "Snow Leopard";
        case 7:
          return "Lion";
        case 8:
          return "Mountain Lion";
        case 9:
          return "Mavericks";
        case 10:
          return "Yosemite";
        case 11:
          return "El Capitan";
        case 12:
          return "Sierra";
        case 13:
          return "High Sierra";
        case 14:
          return "Mojave";
        case 15:
          return "Catalina";
        default:
          return;
      }
  }
  /**
   * Get Android version name
   *    1.5 - Cupcake
   *    1.6 - Donut
   *    2.0 - Eclair
   *    2.1 - Eclair
   *    2.2 - Froyo
   *    2.x - Gingerbread
   *    3.x - Honeycomb
   *    4.0 - Ice Cream Sandwich
   *    4.1 - Jelly Bean
   *    4.4 - KitKat
   *    5.x - Lollipop
   *    6.x - Marshmallow
   *    7.x - Nougat
   *    8.x - Oreo
   *    9.x - Pie
   *
   * @example
   *   getAndroidVersionName("7.0") // 'Nougat'
   *
   * @param  {string} version
   * @return {string} versionName
   */
  static getAndroidVersionName(e) {
    const n = e.split(".").splice(0, 2).map((r) => parseInt(r, 10) || 0);
    if (n.push(0), !(n[0] === 1 && n[1] < 5)) {
      if (n[0] === 1 && n[1] < 6) return "Cupcake";
      if (n[0] === 1 && n[1] >= 6) return "Donut";
      if (n[0] === 2 && n[1] < 2) return "Eclair";
      if (n[0] === 2 && n[1] === 2) return "Froyo";
      if (n[0] === 2 && n[1] > 2) return "Gingerbread";
      if (n[0] === 3) return "Honeycomb";
      if (n[0] === 4 && n[1] < 1) return "Ice Cream Sandwich";
      if (n[0] === 4 && n[1] < 4) return "Jelly Bean";
      if (n[0] === 4 && n[1] >= 4) return "KitKat";
      if (n[0] === 5) return "Lollipop";
      if (n[0] === 6) return "Marshmallow";
      if (n[0] === 7) return "Nougat";
      if (n[0] === 8) return "Oreo";
      if (n[0] === 9) return "Pie";
    }
  }
  /**
   * Get version precisions count
   *
   * @example
   *   getVersionPrecision("1.10.3") // 3
   *
   * @param  {string} version
   * @return {number}
   */
  static getVersionPrecision(e) {
    return e.split(".").length;
  }
  /**
   * Calculate browser version weight
   *
   * @example
   *   compareVersions('1.10.2.1',  '1.8.2.1.90')    // 1
   *   compareVersions('1.010.2.1', '1.09.2.1.90');  // 1
   *   compareVersions('1.10.2.1',  '1.10.2.1');     // 0
   *   compareVersions('1.10.2.1',  '1.0800.2');     // -1
   *   compareVersions('1.10.2.1',  '1.10',  true);  // 0
   *
   * @param {String} versionA versions versions to compare
   * @param {String} versionB versions versions to compare
   * @param {boolean} [isLoose] enable loose comparison
   * @return {Number} comparison result: -1 when versionA is lower,
   * 1 when versionA is bigger, 0 when both equal
   */
  /* eslint consistent-return: 1 */
  static compareVersions(e, n, r = !1) {
    const i = I.getVersionPrecision(e), s = I.getVersionPrecision(n);
    let o = Math.max(i, s), l = 0;
    const u = I.map([e, n], (c) => {
      const a = o - I.getVersionPrecision(c), f = c + new Array(a + 1).join(".0");
      return I.map(f.split("."), (h) => new Array(20 - h.length).join("0") + h).reverse();
    });
    for (r && (l = o - Math.min(i, s)), o -= 1; o >= l; ) {
      if (u[0][o] > u[1][o])
        return 1;
      if (u[0][o] === u[1][o]) {
        if (o === l)
          return 0;
        o -= 1;
      } else if (u[0][o] < u[1][o])
        return -1;
    }
  }
  /**
   * Array::map polyfill
   *
   * @param  {Array} arr
   * @param  {Function} iterator
   * @return {Array}
   */
  static map(e, n) {
    const r = [];
    let i;
    if (Array.prototype.map)
      return Array.prototype.map.call(e, n);
    for (i = 0; i < e.length; i += 1)
      r.push(n(e[i]));
    return r;
  }
  /**
   * Array::find polyfill
   *
   * @param  {Array} arr
   * @param  {Function} predicate
   * @return {Array}
   */
  static find(e, n) {
    let r, i;
    if (Array.prototype.find)
      return Array.prototype.find.call(e, n);
    for (r = 0, i = e.length; r < i; r += 1) {
      const s = e[r];
      if (n(s, r))
        return s;
    }
  }
  /**
   * Object::assign polyfill
   *
   * @param  {Object} obj
   * @param  {Object} ...objs
   * @return {Object}
   */
  static assign(e, ...n) {
    const r = e;
    let i, s;
    if (Object.assign)
      return Object.assign(e, ...n);
    for (i = 0, s = n.length; i < s; i += 1) {
      const o = n[i];
      typeof o == "object" && o !== null && Object.keys(o).forEach((u) => {
        r[u] = o[u];
      });
    }
    return e;
  }
  /**
   * Get short version/alias for a browser name
   *
   * @example
   *   getBrowserAlias('Microsoft Edge') // edge
   *
   * @param  {string} browserName
   * @return {string}
   */
  static getBrowserAlias(e) {
    return zm[e];
  }
  /**
   * Get short version/alias for a browser name
   *
   * @example
   *   getBrowserAlias('edge') // Microsoft Edge
   *
   * @param  {string} browserAlias
   * @return {string}
   */
  static getBrowserTypeByAlias(e) {
    return Ru[e] || "";
  }
}
const _t = /version\/(\d+(\.?_?\d+)+)/i, Dm = [
  /* Googlebot */
  {
    test: [/googlebot/i],
    describe(t) {
      const e = {
        name: "Googlebot"
      }, n = I.getFirstMatch(/googlebot\/(\d+(\.\d+))/i, t) || I.getFirstMatch(_t, t);
      return n && (e.version = n), e;
    }
  },
  /* Opera < 13.0 */
  {
    test: [/opera/i],
    describe(t) {
      const e = {
        name: "Opera"
      }, n = I.getFirstMatch(_t, t) || I.getFirstMatch(/(?:opera)[\s/](\d+(\.?_?\d+)+)/i, t);
      return n && (e.version = n), e;
    }
  },
  /* Opera > 13.0 */
  {
    test: [/opr\/|opios/i],
    describe(t) {
      const e = {
        name: "Opera"
      }, n = I.getFirstMatch(/(?:opr|opios)[\s/](\S+)/i, t) || I.getFirstMatch(_t, t);
      return n && (e.version = n), e;
    }
  },
  {
    test: [/SamsungBrowser/i],
    describe(t) {
      const e = {
        name: "Samsung Internet for Android"
      }, n = I.getFirstMatch(_t, t) || I.getFirstMatch(/(?:SamsungBrowser)[\s/](\d+(\.?_?\d+)+)/i, t);
      return n && (e.version = n), e;
    }
  },
  {
    test: [/Whale/i],
    describe(t) {
      const e = {
        name: "NAVER Whale Browser"
      }, n = I.getFirstMatch(_t, t) || I.getFirstMatch(/(?:whale)[\s/](\d+(?:\.\d+)+)/i, t);
      return n && (e.version = n), e;
    }
  },
  {
    test: [/MZBrowser/i],
    describe(t) {
      const e = {
        name: "MZ Browser"
      }, n = I.getFirstMatch(/(?:MZBrowser)[\s/](\d+(?:\.\d+)+)/i, t) || I.getFirstMatch(_t, t);
      return n && (e.version = n), e;
    }
  },
  {
    test: [/focus/i],
    describe(t) {
      const e = {
        name: "Focus"
      }, n = I.getFirstMatch(/(?:focus)[\s/](\d+(?:\.\d+)+)/i, t) || I.getFirstMatch(_t, t);
      return n && (e.version = n), e;
    }
  },
  {
    test: [/swing/i],
    describe(t) {
      const e = {
        name: "Swing"
      }, n = I.getFirstMatch(/(?:swing)[\s/](\d+(?:\.\d+)+)/i, t) || I.getFirstMatch(_t, t);
      return n && (e.version = n), e;
    }
  },
  {
    test: [/coast/i],
    describe(t) {
      const e = {
        name: "Opera Coast"
      }, n = I.getFirstMatch(_t, t) || I.getFirstMatch(/(?:coast)[\s/](\d+(\.?_?\d+)+)/i, t);
      return n && (e.version = n), e;
    }
  },
  {
    test: [/opt\/\d+(?:.?_?\d+)+/i],
    describe(t) {
      const e = {
        name: "Opera Touch"
      }, n = I.getFirstMatch(/(?:opt)[\s/](\d+(\.?_?\d+)+)/i, t) || I.getFirstMatch(_t, t);
      return n && (e.version = n), e;
    }
  },
  {
    test: [/yabrowser/i],
    describe(t) {
      const e = {
        name: "Yandex Browser"
      }, n = I.getFirstMatch(/(?:yabrowser)[\s/](\d+(\.?_?\d+)+)/i, t) || I.getFirstMatch(_t, t);
      return n && (e.version = n), e;
    }
  },
  {
    test: [/ucbrowser/i],
    describe(t) {
      const e = {
        name: "UC Browser"
      }, n = I.getFirstMatch(_t, t) || I.getFirstMatch(/(?:ucbrowser)[\s/](\d+(\.?_?\d+)+)/i, t);
      return n && (e.version = n), e;
    }
  },
  {
    test: [/Maxthon|mxios/i],
    describe(t) {
      const e = {
        name: "Maxthon"
      }, n = I.getFirstMatch(_t, t) || I.getFirstMatch(/(?:Maxthon|mxios)[\s/](\d+(\.?_?\d+)+)/i, t);
      return n && (e.version = n), e;
    }
  },
  {
    test: [/epiphany/i],
    describe(t) {
      const e = {
        name: "Epiphany"
      }, n = I.getFirstMatch(_t, t) || I.getFirstMatch(/(?:epiphany)[\s/](\d+(\.?_?\d+)+)/i, t);
      return n && (e.version = n), e;
    }
  },
  {
    test: [/puffin/i],
    describe(t) {
      const e = {
        name: "Puffin"
      }, n = I.getFirstMatch(_t, t) || I.getFirstMatch(/(?:puffin)[\s/](\d+(\.?_?\d+)+)/i, t);
      return n && (e.version = n), e;
    }
  },
  {
    test: [/sleipnir/i],
    describe(t) {
      const e = {
        name: "Sleipnir"
      }, n = I.getFirstMatch(_t, t) || I.getFirstMatch(/(?:sleipnir)[\s/](\d+(\.?_?\d+)+)/i, t);
      return n && (e.version = n), e;
    }
  },
  {
    test: [/k-meleon/i],
    describe(t) {
      const e = {
        name: "K-Meleon"
      }, n = I.getFirstMatch(_t, t) || I.getFirstMatch(/(?:k-meleon)[\s/](\d+(\.?_?\d+)+)/i, t);
      return n && (e.version = n), e;
    }
  },
  {
    test: [/micromessenger/i],
    describe(t) {
      const e = {
        name: "WeChat"
      }, n = I.getFirstMatch(/(?:micromessenger)[\s/](\d+(\.?_?\d+)+)/i, t) || I.getFirstMatch(_t, t);
      return n && (e.version = n), e;
    }
  },
  {
    test: [/qqbrowser/i],
    describe(t) {
      const e = {
        name: /qqbrowserlite/i.test(t) ? "QQ Browser Lite" : "QQ Browser"
      }, n = I.getFirstMatch(/(?:qqbrowserlite|qqbrowser)[/](\d+(\.?_?\d+)+)/i, t) || I.getFirstMatch(_t, t);
      return n && (e.version = n), e;
    }
  },
  {
    test: [/msie|trident/i],
    describe(t) {
      const e = {
        name: "Internet Explorer"
      }, n = I.getFirstMatch(/(?:msie |rv:)(\d+(\.?_?\d+)+)/i, t);
      return n && (e.version = n), e;
    }
  },
  {
    test: [/\sedg\//i],
    describe(t) {
      const e = {
        name: "Microsoft Edge"
      }, n = I.getFirstMatch(/\sedg\/(\d+(\.?_?\d+)+)/i, t);
      return n && (e.version = n), e;
    }
  },
  {
    test: [/edg([ea]|ios)/i],
    describe(t) {
      const e = {
        name: "Microsoft Edge"
      }, n = I.getSecondMatch(/edg([ea]|ios)\/(\d+(\.?_?\d+)+)/i, t);
      return n && (e.version = n), e;
    }
  },
  {
    test: [/vivaldi/i],
    describe(t) {
      const e = {
        name: "Vivaldi"
      }, n = I.getFirstMatch(/vivaldi\/(\d+(\.?_?\d+)+)/i, t);
      return n && (e.version = n), e;
    }
  },
  {
    test: [/seamonkey/i],
    describe(t) {
      const e = {
        name: "SeaMonkey"
      }, n = I.getFirstMatch(/seamonkey\/(\d+(\.?_?\d+)+)/i, t);
      return n && (e.version = n), e;
    }
  },
  {
    test: [/sailfish/i],
    describe(t) {
      const e = {
        name: "Sailfish"
      }, n = I.getFirstMatch(/sailfish\s?browser\/(\d+(\.\d+)?)/i, t);
      return n && (e.version = n), e;
    }
  },
  {
    test: [/silk/i],
    describe(t) {
      const e = {
        name: "Amazon Silk"
      }, n = I.getFirstMatch(/silk\/(\d+(\.?_?\d+)+)/i, t);
      return n && (e.version = n), e;
    }
  },
  {
    test: [/phantom/i],
    describe(t) {
      const e = {
        name: "PhantomJS"
      }, n = I.getFirstMatch(/phantomjs\/(\d+(\.?_?\d+)+)/i, t);
      return n && (e.version = n), e;
    }
  },
  {
    test: [/slimerjs/i],
    describe(t) {
      const e = {
        name: "SlimerJS"
      }, n = I.getFirstMatch(/slimerjs\/(\d+(\.?_?\d+)+)/i, t);
      return n && (e.version = n), e;
    }
  },
  {
    test: [/blackberry|\bbb\d+/i, /rim\stablet/i],
    describe(t) {
      const e = {
        name: "BlackBerry"
      }, n = I.getFirstMatch(_t, t) || I.getFirstMatch(/blackberry[\d]+\/(\d+(\.?_?\d+)+)/i, t);
      return n && (e.version = n), e;
    }
  },
  {
    test: [/(web|hpw)[o0]s/i],
    describe(t) {
      const e = {
        name: "WebOS Browser"
      }, n = I.getFirstMatch(_t, t) || I.getFirstMatch(/w(?:eb)?[o0]sbrowser\/(\d+(\.?_?\d+)+)/i, t);
      return n && (e.version = n), e;
    }
  },
  {
    test: [/bada/i],
    describe(t) {
      const e = {
        name: "Bada"
      }, n = I.getFirstMatch(/dolfin\/(\d+(\.?_?\d+)+)/i, t);
      return n && (e.version = n), e;
    }
  },
  {
    test: [/tizen/i],
    describe(t) {
      const e = {
        name: "Tizen"
      }, n = I.getFirstMatch(/(?:tizen\s?)?browser\/(\d+(\.?_?\d+)+)/i, t) || I.getFirstMatch(_t, t);
      return n && (e.version = n), e;
    }
  },
  {
    test: [/qupzilla/i],
    describe(t) {
      const e = {
        name: "QupZilla"
      }, n = I.getFirstMatch(/(?:qupzilla)[\s/](\d+(\.?_?\d+)+)/i, t) || I.getFirstMatch(_t, t);
      return n && (e.version = n), e;
    }
  },
  {
    test: [/firefox|iceweasel|fxios/i],
    describe(t) {
      const e = {
        name: "Firefox"
      }, n = I.getFirstMatch(/(?:firefox|iceweasel|fxios)[\s/](\d+(\.?_?\d+)+)/i, t);
      return n && (e.version = n), e;
    }
  },
  {
    test: [/electron/i],
    describe(t) {
      const e = {
        name: "Electron"
      }, n = I.getFirstMatch(/(?:electron)\/(\d+(\.?_?\d+)+)/i, t);
      return n && (e.version = n), e;
    }
  },
  {
    test: [/MiuiBrowser/i],
    describe(t) {
      const e = {
        name: "Miui"
      }, n = I.getFirstMatch(/(?:MiuiBrowser)[\s/](\d+(\.?_?\d+)+)/i, t);
      return n && (e.version = n), e;
    }
  },
  {
    test: [/chromium/i],
    describe(t) {
      const e = {
        name: "Chromium"
      }, n = I.getFirstMatch(/(?:chromium)[\s/](\d+(\.?_?\d+)+)/i, t) || I.getFirstMatch(_t, t);
      return n && (e.version = n), e;
    }
  },
  {
    test: [/chrome|crios|crmo/i],
    describe(t) {
      const e = {
        name: "Chrome"
      }, n = I.getFirstMatch(/(?:chrome|crios|crmo)\/(\d+(\.?_?\d+)+)/i, t);
      return n && (e.version = n), e;
    }
  },
  {
    test: [/GSA/i],
    describe(t) {
      const e = {
        name: "Google Search"
      }, n = I.getFirstMatch(/(?:GSA)\/(\d+(\.?_?\d+)+)/i, t);
      return n && (e.version = n), e;
    }
  },
  /* Android Browser */
  {
    test(t) {
      const e = !t.test(/like android/i), n = t.test(/android/i);
      return e && n;
    },
    describe(t) {
      const e = {
        name: "Android Browser"
      }, n = I.getFirstMatch(_t, t);
      return n && (e.version = n), e;
    }
  },
  /* PlayStation 4 */
  {
    test: [/playstation 4/i],
    describe(t) {
      const e = {
        name: "PlayStation 4"
      }, n = I.getFirstMatch(_t, t);
      return n && (e.version = n), e;
    }
  },
  /* Safari */
  {
    test: [/safari|applewebkit/i],
    describe(t) {
      const e = {
        name: "Safari"
      }, n = I.getFirstMatch(_t, t);
      return n && (e.version = n), e;
    }
  },
  /* Something else */
  {
    test: [/.*/i],
    describe(t) {
      const e = /^(.*)\/(.*) /, n = /^(.*)\/(.*)[ \t]\((.*)/, i = t.search("\\(") !== -1 ? n : e;
      return {
        name: I.getFirstMatch(i, t),
        version: I.getSecondMatch(i, t)
      };
    }
  }
], Vm = [
  /* Roku */
  {
    test: [/Roku\/DVP/],
    describe(t) {
      const e = I.getFirstMatch(/Roku\/DVP-(\d+\.\d+)/i, t);
      return {
        name: At.Roku,
        version: e
      };
    }
  },
  /* Windows Phone */
  {
    test: [/windows phone/i],
    describe(t) {
      const e = I.getFirstMatch(/windows phone (?:os)?\s?(\d+(\.\d+)*)/i, t);
      return {
        name: At.WindowsPhone,
        version: e
      };
    }
  },
  /* Windows */
  {
    test: [/windows /i],
    describe(t) {
      const e = I.getFirstMatch(/Windows ((NT|XP)( \d\d?.\d)?)/i, t), n = I.getWindowsVersionName(e);
      return {
        name: At.Windows,
        version: e,
        versionName: n
      };
    }
  },
  /* Firefox on iPad */
  {
    test: [/Macintosh(.*?) FxiOS(.*?)\//],
    describe(t) {
      const e = {
        name: At.iOS
      }, n = I.getSecondMatch(/(Version\/)(\d[\d.]+)/, t);
      return n && (e.version = n), e;
    }
  },
  /* macOS */
  {
    test: [/macintosh/i],
    describe(t) {
      const e = I.getFirstMatch(/mac os x (\d+(\.?_?\d+)+)/i, t).replace(/[_\s]/g, "."), n = I.getMacOSVersionName(e), r = {
        name: At.MacOS,
        version: e
      };
      return n && (r.versionName = n), r;
    }
  },
  /* iOS */
  {
    test: [/(ipod|iphone|ipad)/i],
    describe(t) {
      const e = I.getFirstMatch(/os (\d+([_\s]\d+)*) like mac os x/i, t).replace(/[_\s]/g, ".");
      return {
        name: At.iOS,
        version: e
      };
    }
  },
  /* Android */
  {
    test(t) {
      const e = !t.test(/like android/i), n = t.test(/android/i);
      return e && n;
    },
    describe(t) {
      const e = I.getFirstMatch(/android[\s/-](\d+(\.\d+)*)/i, t), n = I.getAndroidVersionName(e), r = {
        name: At.Android,
        version: e
      };
      return n && (r.versionName = n), r;
    }
  },
  /* WebOS */
  {
    test: [/(web|hpw)[o0]s/i],
    describe(t) {
      const e = I.getFirstMatch(/(?:web|hpw)[o0]s\/(\d+(\.\d+)*)/i, t), n = {
        name: At.WebOS
      };
      return e && e.length && (n.version = e), n;
    }
  },
  /* BlackBerry */
  {
    test: [/blackberry|\bbb\d+/i, /rim\stablet/i],
    describe(t) {
      const e = I.getFirstMatch(/rim\stablet\sos\s(\d+(\.\d+)*)/i, t) || I.getFirstMatch(/blackberry\d+\/(\d+([_\s]\d+)*)/i, t) || I.getFirstMatch(/\bbb(\d+)/i, t);
      return {
        name: At.BlackBerry,
        version: e
      };
    }
  },
  /* Bada */
  {
    test: [/bada/i],
    describe(t) {
      const e = I.getFirstMatch(/bada\/(\d+(\.\d+)*)/i, t);
      return {
        name: At.Bada,
        version: e
      };
    }
  },
  /* Tizen */
  {
    test: [/tizen/i],
    describe(t) {
      const e = I.getFirstMatch(/tizen[/\s](\d+(\.\d+)*)/i, t);
      return {
        name: At.Tizen,
        version: e
      };
    }
  },
  /* Linux */
  {
    test: [/linux/i],
    describe() {
      return {
        name: At.Linux
      };
    }
  },
  /* Chrome OS */
  {
    test: [/CrOS/],
    describe() {
      return {
        name: At.ChromeOS
      };
    }
  },
  /* Playstation 4 */
  {
    test: [/PlayStation 4/],
    describe(t) {
      const e = I.getFirstMatch(/PlayStation 4[/\s](\d+(\.\d+)*)/i, t);
      return {
        name: At.PlayStation4,
        version: e
      };
    }
  }
], qm = [
  /* Googlebot */
  {
    test: [/googlebot/i],
    describe() {
      return {
        type: "bot",
        vendor: "Google"
      };
    }
  },
  /* Huawei */
  {
    test: [/huawei/i],
    describe(t) {
      const e = I.getFirstMatch(/(can-l01)/i, t) && "Nova", n = {
        type: Nt.mobile,
        vendor: "Huawei"
      };
      return e && (n.model = e), n;
    }
  },
  /* Nexus Tablet */
  {
    test: [/nexus\s*(?:7|8|9|10).*/i],
    describe() {
      return {
        type: Nt.tablet,
        vendor: "Nexus"
      };
    }
  },
  /* iPad */
  {
    test: [/ipad/i],
    describe() {
      return {
        type: Nt.tablet,
        vendor: "Apple",
        model: "iPad"
      };
    }
  },
  /* Firefox on iPad */
  {
    test: [/Macintosh(.*?) FxiOS(.*?)\//],
    describe() {
      return {
        type: Nt.tablet,
        vendor: "Apple",
        model: "iPad"
      };
    }
  },
  /* Amazon Kindle Fire */
  {
    test: [/kftt build/i],
    describe() {
      return {
        type: Nt.tablet,
        vendor: "Amazon",
        model: "Kindle Fire HD 7"
      };
    }
  },
  /* Another Amazon Tablet with Silk */
  {
    test: [/silk/i],
    describe() {
      return {
        type: Nt.tablet,
        vendor: "Amazon"
      };
    }
  },
  /* Tablet */
  {
    test: [/tablet(?! pc)/i],
    describe() {
      return {
        type: Nt.tablet
      };
    }
  },
  /* iPod/iPhone */
  {
    test(t) {
      const e = t.test(/ipod|iphone/i), n = t.test(/like (ipod|iphone)/i);
      return e && !n;
    },
    describe(t) {
      const e = I.getFirstMatch(/(ipod|iphone)/i, t);
      return {
        type: Nt.mobile,
        vendor: "Apple",
        model: e
      };
    }
  },
  /* Nexus Mobile */
  {
    test: [/nexus\s*[0-6].*/i, /galaxy nexus/i],
    describe() {
      return {
        type: Nt.mobile,
        vendor: "Nexus"
      };
    }
  },
  /* Mobile */
  {
    test: [/[^-]mobi/i],
    describe() {
      return {
        type: Nt.mobile
      };
    }
  },
  /* BlackBerry */
  {
    test(t) {
      return t.getBrowserName(!0) === "blackberry";
    },
    describe() {
      return {
        type: Nt.mobile,
        vendor: "BlackBerry"
      };
    }
  },
  /* Bada */
  {
    test(t) {
      return t.getBrowserName(!0) === "bada";
    },
    describe() {
      return {
        type: Nt.mobile
      };
    }
  },
  /* Windows Phone */
  {
    test(t) {
      return t.getBrowserName() === "windows phone";
    },
    describe() {
      return {
        type: Nt.mobile,
        vendor: "Microsoft"
      };
    }
  },
  /* Android Tablet */
  {
    test(t) {
      const e = Number(String(t.getOSVersion()).split(".")[0]);
      return t.getOSName(!0) === "android" && e >= 3;
    },
    describe() {
      return {
        type: Nt.tablet
      };
    }
  },
  /* Android Mobile */
  {
    test(t) {
      return t.getOSName(!0) === "android";
    },
    describe() {
      return {
        type: Nt.mobile
      };
    }
  },
  /* desktop */
  {
    test(t) {
      return t.getOSName(!0) === "macos";
    },
    describe() {
      return {
        type: Nt.desktop,
        vendor: "Apple"
      };
    }
  },
  /* Windows */
  {
    test(t) {
      return t.getOSName(!0) === "windows";
    },
    describe() {
      return {
        type: Nt.desktop
      };
    }
  },
  /* Linux */
  {
    test(t) {
      return t.getOSName(!0) === "linux";
    },
    describe() {
      return {
        type: Nt.desktop
      };
    }
  },
  /* PlayStation 4 */
  {
    test(t) {
      return t.getOSName(!0) === "playstation 4";
    },
    describe() {
      return {
        type: Nt.tv
      };
    }
  },
  /* Roku */
  {
    test(t) {
      return t.getOSName(!0) === "roku";
    },
    describe() {
      return {
        type: Nt.tv
      };
    }
  }
], Gm = [
  /* EdgeHTML */
  {
    test(t) {
      return t.getBrowserName(!0) === "microsoft edge";
    },
    describe(t) {
      if (/\sedg\//i.test(t))
        return {
          name: Ie.Blink
        };
      const n = I.getFirstMatch(/edge\/(\d+(\.?_?\d+)+)/i, t);
      return {
        name: Ie.EdgeHTML,
        version: n
      };
    }
  },
  /* Trident */
  {
    test: [/trident/i],
    describe(t) {
      const e = {
        name: Ie.Trident
      }, n = I.getFirstMatch(/trident\/(\d+(\.?_?\d+)+)/i, t);
      return n && (e.version = n), e;
    }
  },
  /* Presto */
  {
    test(t) {
      return t.test(/presto/i);
    },
    describe(t) {
      const e = {
        name: Ie.Presto
      }, n = I.getFirstMatch(/presto\/(\d+(\.?_?\d+)+)/i, t);
      return n && (e.version = n), e;
    }
  },
  /* Gecko */
  {
    test(t) {
      const e = t.test(/gecko/i), n = t.test(/like gecko/i);
      return e && !n;
    },
    describe(t) {
      const e = {
        name: Ie.Gecko
      }, n = I.getFirstMatch(/gecko\/(\d+(\.?_?\d+)+)/i, t);
      return n && (e.version = n), e;
    }
  },
  /* Blink */
  {
    test: [/(apple)?webkit\/537\.36/i],
    describe() {
      return {
        name: Ie.Blink
      };
    }
  },
  /* WebKit */
  {
    test: [/(apple)?webkit/i],
    describe(t) {
      const e = {
        name: Ie.WebKit
      }, n = I.getFirstMatch(/webkit\/(\d+(\.?_?\d+)+)/i, t);
      return n && (e.version = n), e;
    }
  }
];
class Ho {
  /**
   * Create instance of Parser
   *
   * @param {String} UA User-Agent string
   * @param {Boolean} [skipParsing=false] parser can skip parsing in purpose of performance
   * improvements if you need to make a more particular parsing
   * like {@link Parser#parseBrowser} or {@link Parser#parsePlatform}
   *
   * @throw {Error} in case of empty UA String
   *
   * @constructor
   */
  constructor(e, n = !1) {
    if (e == null || e === "")
      throw new Error("UserAgent parameter can't be empty");
    this._ua = e, this.parsedResult = {}, n !== !0 && this.parse();
  }
  /**
   * Get UserAgent string of current Parser instance
   * @return {String} User-Agent String of the current <Parser> object
   *
   * @public
   */
  getUA() {
    return this._ua;
  }
  /**
   * Test a UA string for a regexp
   * @param {RegExp} regex
   * @return {Boolean}
   */
  test(e) {
    return e.test(this._ua);
  }
  /**
   * Get parsed browser object
   * @return {Object}
   */
  parseBrowser() {
    this.parsedResult.browser = {};
    const e = I.find(Dm, (n) => {
      if (typeof n.test == "function")
        return n.test(this);
      if (n.test instanceof Array)
        return n.test.some((r) => this.test(r));
      throw new Error("Browser's test function is not valid");
    });
    return e && (this.parsedResult.browser = e.describe(this.getUA())), this.parsedResult.browser;
  }
  /**
   * Get parsed browser object
   * @return {Object}
   *
   * @public
   */
  getBrowser() {
    return this.parsedResult.browser ? this.parsedResult.browser : this.parseBrowser();
  }
  /**
   * Get browser's name
   * @return {String} Browser's name or an empty string
   *
   * @public
   */
  getBrowserName(e) {
    return e ? String(this.getBrowser().name).toLowerCase() || "" : this.getBrowser().name || "";
  }
  /**
   * Get browser's version
   * @return {String} version of browser
   *
   * @public
   */
  getBrowserVersion() {
    return this.getBrowser().version;
  }
  /**
   * Get OS
   * @return {Object}
   *
   * @example
   * this.getOS();
   * {
   *   name: 'macOS',
   *   version: '10.11.12'
   * }
   */
  getOS() {
    return this.parsedResult.os ? this.parsedResult.os : this.parseOS();
  }
  /**
   * Parse OS and save it to this.parsedResult.os
   * @return {*|{}}
   */
  parseOS() {
    this.parsedResult.os = {};
    const e = I.find(Vm, (n) => {
      if (typeof n.test == "function")
        return n.test(this);
      if (n.test instanceof Array)
        return n.test.some((r) => this.test(r));
      throw new Error("Browser's test function is not valid");
    });
    return e && (this.parsedResult.os = e.describe(this.getUA())), this.parsedResult.os;
  }
  /**
   * Get OS name
   * @param {Boolean} [toLowerCase] return lower-cased value
   * @return {String} name of the OS — macOS, Windows, Linux, etc.
   */
  getOSName(e) {
    const { name: n } = this.getOS();
    return e ? String(n).toLowerCase() || "" : n || "";
  }
  /**
   * Get OS version
   * @return {String} full version with dots ('10.11.12', '5.6', etc)
   */
  getOSVersion() {
    return this.getOS().version;
  }
  /**
   * Get parsed platform
   * @return {{}}
   */
  getPlatform() {
    return this.parsedResult.platform ? this.parsedResult.platform : this.parsePlatform();
  }
  /**
   * Get platform name
   * @param {Boolean} [toLowerCase=false]
   * @return {*}
   */
  getPlatformType(e = !1) {
    const { type: n } = this.getPlatform();
    return e ? String(n).toLowerCase() || "" : n || "";
  }
  /**
   * Get parsed platform
   * @return {{}}
   */
  parsePlatform() {
    this.parsedResult.platform = {};
    const e = I.find(qm, (n) => {
      if (typeof n.test == "function")
        return n.test(this);
      if (n.test instanceof Array)
        return n.test.some((r) => this.test(r));
      throw new Error("Browser's test function is not valid");
    });
    return e && (this.parsedResult.platform = e.describe(this.getUA())), this.parsedResult.platform;
  }
  /**
   * Get parsed engine
   * @return {{}}
   */
  getEngine() {
    return this.parsedResult.engine ? this.parsedResult.engine : this.parseEngine();
  }
  /**
   * Get engines's name
   * @return {String} Engines's name or an empty string
   *
   * @public
   */
  getEngineName(e) {
    return e ? String(this.getEngine().name).toLowerCase() || "" : this.getEngine().name || "";
  }
  /**
   * Get parsed platform
   * @return {{}}
   */
  parseEngine() {
    this.parsedResult.engine = {};
    const e = I.find(Gm, (n) => {
      if (typeof n.test == "function")
        return n.test(this);
      if (n.test instanceof Array)
        return n.test.some((r) => this.test(r));
      throw new Error("Browser's test function is not valid");
    });
    return e && (this.parsedResult.engine = e.describe(this.getUA())), this.parsedResult.engine;
  }
  /**
   * Parse full information about the browser
   * @returns {Parser}
   */
  parse() {
    return this.parseBrowser(), this.parseOS(), this.parsePlatform(), this.parseEngine(), this;
  }
  /**
   * Get parsed result
   * @return {ParsedResult}
   */
  getResult() {
    return I.assign({}, this.parsedResult);
  }
  /**
   * Check if parsed browser matches certain conditions
   *
   * @param {Object} checkTree It's one or two layered object,
   * which can include a platform or an OS on the first layer
   * and should have browsers specs on the bottom-laying layer
   *
   * @returns {Boolean|undefined} Whether the browser satisfies the set conditions or not.
   * Returns `undefined` when the browser is no described in the checkTree object.
   *
   * @example
   * const browser = Bowser.getParser(window.navigator.userAgent);
   * if (browser.satisfies({chrome: '>118.01.1322' }))
   * // or with os
   * if (browser.satisfies({windows: { chrome: '>118.01.1322' } }))
   * // or with platforms
   * if (browser.satisfies({desktop: { chrome: '>118.01.1322' } }))
   */
  satisfies(e) {
    const n = {};
    let r = 0;
    const i = {};
    let s = 0;
    if (Object.keys(e).forEach((l) => {
      const u = e[l];
      typeof u == "string" ? (i[l] = u, s += 1) : typeof u == "object" && (n[l] = u, r += 1);
    }), r > 0) {
      const l = Object.keys(n), u = I.find(l, (a) => this.isOS(a));
      if (u) {
        const a = this.satisfies(n[u]);
        if (a !== void 0)
          return a;
      }
      const c = I.find(
        l,
        (a) => this.isPlatform(a)
      );
      if (c) {
        const a = this.satisfies(n[c]);
        if (a !== void 0)
          return a;
      }
    }
    if (s > 0) {
      const l = Object.keys(i), u = I.find(l, (c) => this.isBrowser(c, !0));
      if (u !== void 0)
        return this.compareVersion(i[u]);
    }
  }
  /**
   * Check if the browser name equals the passed string
   * @param browserName The string to compare with the browser name
   * @param [includingAlias=false] The flag showing whether alias will be included into comparison
   * @returns {boolean}
   */
  isBrowser(e, n = !1) {
    const r = this.getBrowserName().toLowerCase();
    let i = e.toLowerCase();
    const s = I.getBrowserTypeByAlias(i);
    return n && s && (i = s.toLowerCase()), i === r;
  }
  compareVersion(e) {
    let n = [0], r = e, i = !1;
    const s = this.getBrowserVersion();
    if (typeof s == "string")
      return e[0] === ">" || e[0] === "<" ? (r = e.substr(1), e[1] === "=" ? (i = !0, r = e.substr(2)) : n = [], e[0] === ">" ? n.push(1) : n.push(-1)) : e[0] === "=" ? r = e.substr(1) : e[0] === "~" && (i = !0, r = e.substr(1)), n.indexOf(
        I.compareVersions(s, r, i)
      ) > -1;
  }
  isOS(e) {
    return this.getOSName(!0) === String(e).toLowerCase();
  }
  isPlatform(e) {
    return this.getPlatformType(!0) === String(e).toLowerCase();
  }
  isEngine(e) {
    return this.getEngineName(!0) === String(e).toLowerCase();
  }
  /**
   * Is anything? Check if the browser is called "anything",
   * the OS called "anything" or the platform called "anything"
   * @param {String} anything
   * @param [includingAlias=false] The flag showing whether alias will be included into comparison
   * @returns {Boolean}
   */
  is(e, n = !1) {
    return this.isBrowser(e, n) || this.isOS(e) || this.isPlatform(e);
  }
  /**
   * Check if any of the given values satisfies this.is(anything)
   * @param {String[]} anythings
   * @returns {Boolean}
   */
  some(e = []) {
    return e.some((n) => this.is(n));
  }
}
/*!
 * Bowser - a browser detector
 * https://github.com/lancedikson/bowser
 * MIT License | (c) Dustin Diaz 2012-2015
 * MIT License | (c) Denis Demchenko 2015-2019
 */
class Hm {
  /**
   * Creates a {@link Parser} instance
   *
   * @param {String} UA UserAgent string
   * @param {Boolean} [skipParsing=false] Will make the Parser postpone parsing until you ask it
   * explicitly. Same as `skipParsing` for {@link Parser}.
   * @returns {Parser}
   * @throws {Error} when UA is not a String
   *
   * @example
   * const parser = Bowser.getParser(window.navigator.userAgent);
   * const result = parser.getResult();
   */
  static getParser(e, n = !1) {
    if (typeof e != "string")
      throw new Error("UserAgent should be a string");
    return new Ho(e, n);
  }
  /**
   * Creates a {@link Parser} instance and runs {@link Parser.getResult} immediately
   *
   * @param UA
   * @return {ParsedResult}
   *
   * @example
   * const result = Bowser.parse(window.navigator.userAgent);
   */
  static parse(e) {
    return new Ho(e).getResult();
  }
  static get BROWSER_MAP() {
    return Ru;
  }
  static get ENGINE_MAP() {
    return Ie;
  }
  static get OS_MAP() {
    return At;
  }
  static get PLATFORMS_MAP() {
    return Nt;
  }
}
const Um = /* @__PURE__ */ Yt("div", { class: "graph-controller__graph-host uninitialised" }, null, -1), Wm = /* @__PURE__ */ fs({
  __name: "GraphComponent",
  setup(t, { expose: e }) {
    const n = Fi(() => {
      const g = document.querySelectorAll("graph-component");
      let E;
      for (let M = 0; M < g.length; M++) {
        const R = g[M], S = Et(R.shadowRoot);
        let O;
        if (S.empty() ? O = Et(
          ".graph-controller__graph-host.uninitialised"
        ) : O = S.select(
          ".graph-controller__graph-host.uninitialised"
        ), !O.empty()) {
          O.classed("uninitialised", !1), E = O;
          break;
        }
      }
      return E === void 0 && (E = Et(
        ".graph-controller__graph-host.uninitialised"
      ), E.classed("uninitialised", !1)), E;
    }), r = Fi(() => {
      let g = n.value.node().parentElement;
      g || (g = n.value.node().getRootNode().host);
      let E = g.getAttribute("id");
      return E || "gc";
    });
    Rl(() => {
      de();
    }), Tl(() => {
      De(), window.addEventListener("resize", Ls);
    }), hs(() => {
      window.removeEventListener("resize", Ls);
    });
    const s = Hm.getParser(window.navigator.userAgent).getPlatformType(!0);
    let o = !1;
    const l = qs(new jo()), u = qs(!1), c = Wr(new $m());
    let a, f = 400, h = 400, p, w, _, v, d, T, P, y, k, L = 0, z = 0, G = 1, Q, X;
    e({
      getGraph: et,
      setGraph: ot,
      printGraph: U,
      setNodeColor: x,
      setLinkColor: D,
      deleteNode: N,
      deleteLink: j,
      setNodeRadius: B,
      setDeletable: Y,
      setLabelEditable: J,
      setNodesLinkPermission: it,
      setNodesFixedPosition: lt,
      setNodeEditability: kt,
      setLinkEditability: pt,
      toggleNodeLabels: Mt,
      toggleLinkLabels: xt,
      toggleZoom: Dt,
      toggleNodePhysics: gt,
      toggleFixedLinkDistance: mt,
      toggleGraphEditingInGUI: Zt,
      resetView: vn
    });
    function et(g = "json", E = !0, M = !0, R = !0) {
      if (g.toLowerCase() === "json")
        return JSON.parse(
          l.value.toJSON(
            c.showLinkLabels,
            c.showLinkLabels,
            E,
            E,
            M,
            R,
            R
          )
        );
      if (g.toLowerCase() === "tgf")
        return l.value.toTGF(c.showNodeLabels, c.showLinkLabels, !0, !0);
      console.error('Invalid format while using getGraph(). Please choose "JSON" or "TGF".');
    }
    function ot(g) {
      typeof g == "object" || typeof g == "string" ? $u(g) : Is();
    }
    function U(g = "json", E = !0, M = !0, R = !0) {
      g.toLowerCase() === "json" ? console.log(
        l.value.toJSON(
          c.showLinkLabels,
          c.showLinkLabels,
          E,
          E,
          M,
          R,
          R
        )
      ) : console.log(l.value.toTGF(c.showNodeLabels, c.showLinkLabels));
    }
    function x(g, E) {
      if (E !== void 0) {
        const R = (Array.isArray(E) ? E : [E]).map(Number);
        for (const S of R)
          d.selectAll("circle").filter((O) => O.id === S).each((O) => O.color = g).style("fill", g);
      } else
        d.selectAll("circle").each((M) => M.color = g).style("fill", g);
    }
    function D(g, E) {
      if (E) {
        const M = Array.isArray(E) ? E : [E];
        Ps(M);
        for (const R of M)
          v.selectAll(".graph-controller__link").filter((S) => S.id === R).each((S) => S.color = g).style("stroke", g);
      } else
        Ps(l.value.links.map((M) => M.id)), v.selectAll(".graph-controller__link").each((M) => M.color = g).style("stroke", g);
      Xi(_, r.value, c, g);
    }
    function N(g) {
      const E = Array.isArray(g) ? g : [g];
      for (const M of E)
        d.selectAll("circle").filter((R) => R.id === M).each(function(R) {
          let S = l.value.removeNode(R);
          if (S !== void 0) {
            let [O, wt] = S;
            xi(O, n.value), wt.forEach(($t) => {
              Mn($t, n.value);
            });
          }
        });
      u.value = l.value.nodes.length > 0;
    }
    function j(g) {
      const E = Array.isArray(g) ? g : [g];
      for (const M of E)
        v.selectAll("path").filter((R) => R.id === M).each(function(R) {
          let S = l.value.removeLink(R);
          S !== void 0 && Mn(S, n.value);
        });
    }
    function B(g) {
      g > 0 ? (c.nodeRadius = g, vn()) : $n("Invalid Radius", "The radius should be greater than zero.");
    }
    function Y(g, E) {
      if (E !== void 0) {
        const [M, R] = hr(E);
        for (const S of M)
          d.filter((O) => O.id === S).each((O) => {
            O.deletable = g;
          });
        for (const S of R)
          v.filter((O) => O.id === S).each((O) => {
            O.deletable = g;
          });
      } else
        d.each((M) => {
          M.deletable = g;
        }), v.each((M) => {
          M.deletable = g;
        });
    }
    function J(g, E) {
      if (E !== void 0) {
        const [M, R] = hr(E);
        for (const S of M)
          d.filter((O) => O.id === S).each((O) => {
            O.labelEditable = g;
          });
        for (const S of R)
          v.filter((O) => O.id === S).each((O) => {
            O.labelEditable = g;
          });
      } else
        d.each((M) => {
          M.labelEditable = g;
        }), v.each((M) => {
          M.labelEditable = g;
        });
    }
    function it(g, E, M) {
      if (M !== void 0) {
        const [R, S] = hr(M);
        for (const O of R)
          d.filter((wt) => wt.id === O).each((wt) => {
            wt.allowIncomingLinks = g, wt.allowOutgoingLinks = E;
          });
      } else
        d.each((R) => {
          R.allowIncomingLinks = g, R.allowOutgoingLinks = E;
        });
    }
    function lt(g, E) {
      if (E !== void 0) {
        const [M, R] = hr(E);
        for (const S of M)
          d.filter((O) => O.id === S).each((O) => {
            dr(O, g);
          });
      } else
        d.each((M) => {
          dr(M, g);
        });
    }
    function kt(g, E) {
      if (E !== void 0) {
        const R = (Array.isArray(E) ? E : [E]).map(Number);
        for (const S of R)
          d.selectAll("circle").filter((O) => O.id === S).each(function(O) {
            dr(O, g.fixedPosition), O.deletable = g.deletable ?? O.deletable, O.labelEditable = g.labelEditable ?? O.labelEditable, O.allowIncomingLinks = g.allowIncomingLinks ?? O.allowIncomingLinks, O.allowOutgoingLinks = g.allowOutgoingLinks ?? O.allowOutgoingLinks;
          });
      } else
        d.selectAll("circle").each(function(M) {
          dr(M, g.fixedPosition), M.deletable = g.deletable ?? M.deletable, M.labelEditable = g.labelEditable ?? M.labelEditable, M.allowIncomingLinks = g.allowIncomingLinks ?? M.allowIncomingLinks, M.allowOutgoingLinks = g.allowOutgoingLinks ?? M.allowOutgoingLinks;
        });
      Ki(
        ["fixedPosition", "deletable", "labelEditable", "allowIncomingLinks", "allowOutgoingLinks"],
        Object.keys(g)
      );
    }
    function pt(g, E) {
      if (E) {
        const M = Array.isArray(E) ? E : [E];
        for (const R of M)
          v.selectAll(".graph-controller__link").filter((S) => S.id === R).each(function(S) {
            S.deletable = g.deletable ?? S.deletable, S.labelEditable = g.labelEditable ?? S.labelEditable;
          });
      } else
        v.selectAll(".graph-controller__link").each(function(M) {
          M.deletable = g.deletable ?? M.deletable, M.labelEditable = g.labelEditable ?? M.labelEditable;
        });
      Ki(["deletable", "labelEditable"], Object.keys(g));
    }
    function gt(g) {
      c.nodePhysicsEnabled = g, xu(a, g, f, h);
    }
    function mt(g) {
      c.fixedLinkDistanceEnabled = g, Eu(a, l.value, c, g);
    }
    function xt(g) {
      c.showLinkLabels = g;
    }
    function Mt(g) {
      c.showNodeLabels = g;
    }
    function Dt(g) {
      c.zoomEnabled = g, vn();
    }
    function Zt(g) {
      c.isGraphEditableInGUI = g;
    }
    function de() {
      const g = (E) => E === "false" ? !1 : !!E;
      localStorage.showNodeLabels && (c.showNodeLabels = g(localStorage.showNodeLabels)), localStorage.enableNodePhysics && (c.nodePhysicsEnabled = g(localStorage.enableNodePhysics)), localStorage.showLinkLabels && (c.showLinkLabels = g(localStorage.showLinkLabels)), localStorage.enableFixedLinkDistance && (c.fixedLinkDistanceEnabled = g(localStorage.enableFixedLinkDistance)), localStorage.enableZoom && (c.zoomEnabled = g(localStorage.enableZoom)), localStorage.persistSettings && (c.persistSettingsLocalStorage = g(localStorage.persistSettings));
    }
    function De() {
      f = n.value.node().clientWidth, h = n.value.node().clientHeight, p = qg(
        (g) => m(g, c.zoomEnabled),
        c.zoomEnabled
      ), _ = Jg(
        n.value,
        p,
        (g) => c.isGraphEditableInGUI ? Rt(g) : null,
        (g) => c.isGraphEditableInGUI ? ct(g) : null,
        (g) => {
          c.isGraphEditableInGUI && C(
            ne(g, _.node())[0],
            ne(g, _.node())[1]
          );
        }
      ), nm(_, r.value, c, l.value.getNonDefaultLinkColors()), T = rm(_), v = Zg(_), d = tm(_), a = im(l.value, c, f, h, () => $()), w = Yg(a, f, h, c.nodeRadius), F();
    }
    function m(g, E = !0) {
      E && (L = g.transform.x, z = g.transform.y, G = g.transform.k, _.attr("transform", `translate(${L},${z})scale(${G})`));
    }
    function b(g, E, M, R, S = !0, O = !0) {
      let wt = l.value.createLink(
        g.id,
        E.id,
        M,
        R,
        S,
        O
      );
      wt !== void 0 && Ug(wt, n.value), F();
    }
    function C(g, E, M, R, S, O = { x: !1, y: !1 }, wt = !0, $t = !0, bn = !0, oi = !0) {
      let Au = l.value.createNode(
        g ?? f / 2,
        E ?? h / 2,
        M,
        R,
        S,
        O,
        wt,
        $t,
        bn,
        oi
      );
      Hg(Au, n.value), u.value = !0, F();
    }
    function $() {
      d.attr("transform", (g) => `translate(${g.x},${g.y})`), v.selectAll("path").attr("d", (g) => A(g)), H(), F();
    }
    function A(g) {
      switch (q(g), g.pathType) {
        case Xt.REFLEXIVE:
          return Vo(g.source, [f / 2, h / 2], c);
        case Xt.ARC:
          return ki(g.source, g.target, c);
        case Xt.ARCREVERSE:
          return Go.reverse(ki(g.source, g.target, c));
        case Xt.LINE:
          return Si(g.source, g.target, c);
        case Xt.LINEREVERSE:
          return Go.reverse(Si(g.source, g.target, c));
        default:
          return "";
      }
    }
    function q(g) {
      g.source.id === g.target.id ? g.pathType = Xt.REFLEXIVE : W(g.source, g.target) ? g.pathType = V(g.source, g.target) ? Xt.ARCREVERSE : Xt.ARC : g.pathType = V(g.source, g.target) ? Xt.LINEREVERSE : Xt.LINE;
    }
    function W(g, E) {
      return g.id !== E.id && l.value.links.some((M) => M.target.id === g.id && M.source.id === E.id) && l.value.links.some((M) => M.target.id === E.id && M.source.id === g.id);
    }
    function V(g, E) {
      return g.x > E.x;
    }
    function H() {
      const g = P;
      if (g !== void 0) {
        const E = y;
        if (E !== void 0)
          T.attr("d", () => g.id === E.id ? Vo(g, [f / 2, h / 2], c) : W(g, E) ? Si(g, E, c) : ki(g, E, c));
        else if (k !== void 0) {
          const M = [g.x, g.y];
          T.attr("d", qo(M, k));
        }
      }
    }
    function F(g = 0.5) {
      var E;
      v = v.data(l.value.links, (M) => M.id).join(
        (M) => {
          const R = M.append("g").classed("graph-controller__link-container", !0);
          return R.append("path").classed("graph-controller__link", !0).style("stroke", (S) => S.color ? S.color : "").attr("id", (S) => r.value + "-link-" + S.id).attr(
            "marker-end",
            (S) => S.color ? `url(#${r.value}-link-arrow-` + S.color : `url(#${r.value}-link-arrow)`
          ), R.append("path").classed("graph-controller__click-box", !0).on("dblclick", (S) => {
            ee(S);
          }).on("pointerout", (S) => Jn(S)).on("pointerdown", (S, O) => {
            Kg(O, S.button, n.value), c.isGraphEditableInGUI && Qn(S, O);
          }).on("pointerup", (S, O) => {
            Ve(S, O);
          }), R.append("text").append("textPath").attr(
            "class",
            (S) => S.label ? "graph-controller__link-label" : "graph-controller__link-label-placeholder"
          ).attr("href", (S) => `#${r.value + "-link-" + S.id}`).attr("startOffset", "50%").text((S) => S.label ? S.label : "add label").on("click", (S, O) => {
            c.isGraphEditableInGUI && Rs(S, O);
          }).on("dblclick", (S) => {
            ee(S);
          }), R.append("foreignObject").classed("graph-controller__link-label-mathjax-container", !0).attr("xmlns", "http://www.w3.org/2000/svg").attr("width", 1).attr("height", 1).html(
            (S) => `<div class=${S.label ? "graph-controller__link-label" : "graph-controller__link-label-placeholder"}>
                            </div>`
          ).on("click", (S, O) => {
            c.isGraphEditableInGUI && Rs(S, O);
          }).on("dblclick", (S) => {
            ee(S);
          }), R;
        },
        (M) => (M.selectChild("path").attr("marker-start", function(R) {
          var S;
          if ((S = R.pathType) != null && S.includes("REVERSE")) {
            let O = `url(#${r.value}-link-arrow-reverse`;
            return R.color && (O += "-" + Un(R.color)), O += ")", O;
          } else
            return null;
        }).attr("marker-end", function(R) {
          var S;
          if ((S = R.pathType) != null && S.includes("REVERSE"))
            return null;
          {
            let O = `url(#${r.value}-link-arrow`;
            return R.color && (O += "-" + Un(R.color)), O += ")", O;
          }
        }), M.selectChild("text").attr("class", (R) => {
          var S;
          return `graph-controller__${(S = R.pathType) == null ? void 0 : S.toLowerCase()}-path-text`;
        }).attr("dy", (R) => {
          var S;
          return R.pathType === Xt.REFLEXIVE ? 15 : R.pathType == Xt.LINEREVERSE ? -10 : (S = R.pathType) != null && S.includes("REVERSE") ? 20 : -10;
        }), M.selectChild("text").selectChild("textPath").classed(
          "hidden",
          (R) => !c.showLinkLabels || !R.label && !R.labelEditable
        ).classed("not-editable", !c.isGraphEditableInGUI).attr("startOffset", (R) => {
          var S;
          return (S = R.pathType) != null && S.includes("REVERSE") ? "46%" : "50%";
        }), M.selectChild("text").selectChild("textPath").selectChild("mjx-container").each(function(R) {
          const S = R, O = Et(
            this.parentNode.parentNode.parentNode
          ).selectChild("foreignObject").selectChild("div").attr("class", "graph-controller__link-label").classed(
            "hidden",
            !c.showLinkLabels || !S.label && !S.labelEditable
          ).node(), wt = Et(this).remove().node();
          O == null || O.appendChild(wt);
        }), M.selectChild("text").selectChild("textPath").each(function() {
          const R = this;
          let S = !1;
          R.childNodes.forEach((wt) => {
            var $t;
            (wt == null ? void 0 : wt.nodeType) === Node.TEXT_NODE && (($t = wt == null ? void 0 : wt.textContent) == null ? void 0 : $t.trim()) !== "" && (S = !0);
          }), S || Et(R).text("I").attr(
            "class",
            "graph-controller__link-label-placeholder mjx-hidden"
          );
        }), M.selectChild("text").selectChild("textPath").each(function() {
          const R = this, [S, O] = Cs(R);
          Et(R.parentNode.parentNode).select("foreignObject").attr("x", S).attr("y", O);
        }), M)
      ), d = d.data(l.value.nodes, (M) => M.id).join(
        (M) => {
          const R = M.append("g").classed("graph-controller__node-container", !0).call(w).on("dblclick", (S) => {
            ee(S);
          }).on("pointerenter", (S, O) => Vt(O)).on("pointerout", (S, O) => xe(O)).on("pointerdown", (S, O) => {
            Wg(O, S.button, n.value), c.isGraphEditableInGUI && K(S, O);
          }).on("pointerup", (S, O) => {
            c.isGraphEditableInGUI && ct(S, O);
          });
          return R.append("circle").classed("graph-controller__node", !0).attr("id", (S) => `${r.value + "-node-" + S.id}`).attr("r", c.nodeRadius).style("fill", (S) => S.color ? S.color : ""), R.append("foreignObject").classed("graph-controller__node-label-container", !0).attr("xmlns", "http://www.w3.org/2000/svg").attr("width", 2 * c.nodeRadius).attr("height", 2 * c.nodeRadius).attr("x", -c.nodeRadius).attr("y", -c.nodeRadius).html(
            (S) => `<div class=${S.label ? "graph-controller__node-label" : "graph-controller__node-label-placeholder"}>
                                ${S.label ? S.label : "add label"}
                         </div>`
          ).on("click", (S, O) => {
            c.isGraphEditableInGUI && Tu(S, O);
          }).on("dblclick", (S) => {
            ee(S);
          }).on("pointerenter", (S, O) => Vt(O)).on("pointerout", (S, O) => xe(O)), R;
        },
        (M) => (M.selectChild("circle").attr("r", c.nodeRadius), M.selectChild("foreignObject").selectChild("div").classed(
          "hidden",
          (R) => !c.showNodeLabels || !R.label && !R.labelEditable
        ).classed("not-editable", !c.isGraphEditableInGUI), M)
      ), (E = window.MathJax) != null && E.version && window.MathJax.typeset(), a.nodes(l.value.nodes), a.alpha(g).restart();
    }
    function K(g, E) {
      (g.button === 2 || g.pointerType === "touch") && (Fo(g), E.allowOutgoingLinks && nt(E), E.deletable && (Q = setTimeout(() => {
        y = void 0, tt(E);
      }, 250)));
    }
    function tt(g) {
      let E = n.value.node().querySelector(`#${r.value + "-node-" + g.id}`);
      Et(E).classed("on-deletion", !0);
      let M = Et(E.parentElement);
      M.select("g.arc").remove();
      let R = Cg().outerRadius(c.nodeRadius + 4).innerRadius(c.nodeRadius), S = [{ startAngle: 0, endAngle: 0 }];
      M.append("g").attr("class", "arc").selectAll("path.arc").data(S).enter().append("path").attr("class", "arc").style("fill", "black").style("opacity", 0.7).transition().duration(750).ease(Op).attrTween("d", function(wt) {
        let $t = { startAngle: 0, endAngle: 2 * Math.PI }, bn = vs(wt, $t);
        return function(oi) {
          return R(bn(oi));
        };
      }).on("end", () => Z(g));
    }
    function Z(g) {
      if (c.isGraphEditableInGUI) {
        let E = l.value.removeNode(g);
        if (E !== void 0) {
          let [M, R] = E;
          xi(M, n.value), R.forEach((S) => {
            Mn(S, n.value);
          });
        }
        u.value = l.value.nodes.length > 0, si(), F();
      }
    }
    function nt(g) {
      const E = [g.x, g.y];
      k = E, P = g, T.attr("marker-end", `url(#${r.value}-draggable-link-arrow)`).classed("hidden", !1).attr("d", qo(E, E)), F();
    }
    function ct(g, E = void 0) {
      ee(g), clearTimeout(Q), E && ht(E), vt();
    }
    function ht(g) {
      let E = n.value.node().querySelector(`#${r.value + "-node-" + g.id}`).parentElement, M = Et(E);
      M.select("circle").classed("on-deletion", !1), M.select("g.arc").select("path.arc").interrupt().remove();
    }
    function vt() {
      const g = P, E = y;
      si(), !(g === void 0 || E === void 0) && b(g, E);
    }
    function Rt(g) {
      if (ee(g), P !== void 0) {
        const E = Hh(g, n.value.node())[0];
        k = [(E[0] - L) / G, (E[1] - z) / G], H();
      }
    }
    function Vt(g) {
      g.allowIncomingLinks && (y = g);
    }
    function xe(g) {
      g && ht(g), y = void 0, clearTimeout(Q);
    }
    function Jn(g) {
      ee(g), clearTimeout(X);
    }
    function Ve(g, E) {
      ee(g), clearTimeout(X), (g.button === 2 || g.pointerType === "touch") && E.deletable && Zn(E);
    }
    function Qn(g, E) {
      (g.button === 2 || g.pointerType === "touch") && (Fo(g), E.deletable && (X = setTimeout(() => {
        It(E);
      }, 250)));
    }
    function It(g) {
      let E = n.value.node().querySelector(`#${r.value + "-link-" + g.id}`);
      if (Et(E).classed("on-deletion", !0), E instanceof SVGPathElement) {
        let M = Et(E), R = E.getTotalLength(), S = E.parentElement.querySelector("text"), O = Array.from(S.classList).some(
          (bn) => bn.includes("reverse")
        ), wt = 0, $t = O ? R : -R;
        M.attr("stroke-dasharray", R).attr("stroke-dashoffset", wt).transition().duration(750).attr("stroke-dashoffset", $t).on("end", () => te(g));
      }
    }
    function te(g) {
      let E = g.color;
      if (c.isGraphEditableInGUI) {
        let M = l.value.removeLink(g);
        M !== void 0 && Mn(M, n.value), E && (l.value.hasNonDefaultLinkColor(E) || Ei(_, r.value, E));
      }
    }
    function Zn(g) {
      let E = n.value.node().querySelector(`#${r.value + "-link-" + g.id}`);
      if (Et(E).classed("on-deletion") && E instanceof SVGPathElement) {
        let M = Et(E), R = E.getTotalLength();
        M.attr("stroke-dasharray", R).attr("stroke-dashoffset", R).transition().attr("stroke-dashoffset", 0).on("end", () => {
          M.attr("stroke-dasharray", null).attr("stroke-dashoffset", null);
        });
      }
      Et(E).classed("on-deletion", !1);
    }
    function Tu(g, E) {
      if (E.labelEditable) {
        const R = (g == null ? void 0 : g.target).closest("div");
        Ts(E, R, [E.x, E.y]);
      }
    }
    function Rs(g, E) {
      if (E.labelEditable) {
        let M = g.target, R;
        M.nodeName === "textPath" ? R = M : R = M.closest(".graph-controller__link-container").querySelector("textPath");
        let S = Cs(R);
        Ts(E, R, S);
      }
    }
    function Ts(g, E, M) {
      let R = g instanceof bu ? "node" : "link";
      const S = document.createElement("input");
      S.setAttribute("class", "graph-controller__label-input"), S.setAttribute("id", `${R}-label-input-field`), g.label == null ? S.value = "" : S.value = g.label, S.placeholder = `Enter ${R} label`;
      const O = document.createElementNS("http://www.w3.org/2000/svg", "foreignObject");
      O.setAttribute("width", "100%"), O.setAttribute("height", "100%"), O.setAttribute("x", `${M[0] - 90}`), O.setAttribute("y", `${M[1] - 12}`), O.append(S), n.value.select("svg").select("g").node().append(O), S.focus(), s !== "desktop" && (o = !0), S.ondblclick = function($t) {
        ee($t);
      };
      let wt = !1;
      S.onkeyup = function($t) {
        $t.key === "Enter" ? (Xg(g, S.value, n.value), wt = !0, S.blur()) : $t.key === "Escape" && (S.value = "", S.blur());
      }, S.onblur = function() {
        wt && (R === "link" && Cu(E), S.value === "" ? Lu(E, g, R) : (Iu(S, E, g, R), R === "node" && Pu(E))), O.remove(), s !== "desktop" && (o = !1);
      };
    }
    function Cu(g) {
      var M;
      const E = g.closest(".graph-controller__link-container");
      (M = E.querySelector("mjx-container")) == null || M.remove(), E.querySelector("div").setAttribute("class", "graph-controller__link-label-placeholder");
    }
    function Pu(g) {
      let E = g.closest(
        ".graph-controller__node-container"
      );
      const M = E.parentElement;
      E.remove(), M.append(E);
    }
    function Lu(g, E, M) {
      g.setAttribute(
        "class",
        `graph-controller__${M}-label-placeholder`
      ), g.textContent = "add label", E.label = void 0;
    }
    function Iu(g, E, M, R) {
      E.setAttribute("class", `graph-controller__${R}-label`), E.textContent = g.value.trim(), M.label = E.textContent;
    }
    function Cs(g) {
      let E = n.value.select("svg").node().getBoundingClientRect(), M = g.getBoundingClientRect(), R = (M.x - E.x - L) / G, S = (M.y - E.y - z) / G;
      return [R, S];
    }
    function si() {
      T == null || T.classed("hidden", !0).attr("marker-end", "null"), P = void 0, y = void 0, k = void 0;
    }
    function $u(g) {
      let E, M;
      try {
        if (typeof g == "string")
          [E, M] = Om(g);
        else if (typeof g == "object")
          [E, M] = Am(g);
        else {
          $n("Invalid graph import type:", "Must either be TGF or JSON.");
          return;
        }
      } catch (R) {
        $n("Error during parsing:", `Invalid data format:
` + R);
        return;
      }
      Is(), Ou(E, M);
    }
    function Ou(g, E) {
      for (let R of g)
        C(
          R.x,
          R.y,
          R.idImported,
          R.label,
          R.color,
          R.fixedPosition,
          R.deletable,
          R.labelEditable,
          R.allowIncomingLinks,
          R.allowOutgoingLinks
        );
      const M = (R) => l.value.nodes.find((S) => S.idImported === R);
      for (let R of E) {
        let S = M(R.sourceIdImported), O = M(R.targetIdImported);
        S && O && (b(
          S,
          O,
          R.label,
          R.color,
          R.deletable,
          R.labelEditable
        ), R.color && Xi(_, r.value, c, R.color));
      }
    }
    function Ps(g) {
      for (let E of g) {
        const M = l.value.links.filter((R) => R.id === E).map((R) => R.color).shift();
        M && (l.value.hasNonDefaultLinkColor(M, E) ? l.value.getLinkIdsWithNonDefaultLinkColors(
          M,
          E
        ).every(
          (O) => g.includes(O)
        ) && Ei(_, r.value, M) : Ei(_, r.value, M));
      }
    }
    function vn() {
      a.stop(), n.value.selectChildren().remove(), p = void 0, L = 0, z = 0, G = 1, _ = void 0, T = void 0, v = void 0, d = void 0, a = void 0, si(), de(), De();
    }
    function Ls() {
      o || vn();
    }
    function Is() {
      l.value.links.forEach((g) => Mn(g, n.value)), l.value.nodes.forEach((g) => xi(g, n.value)), l.value = new jo(), u.value = !1, vn();
    }
    return (g, E) => (Pe(), Le(re, null, [
      Um,
      wr(Yt("div", null, [
        _e(cf, {
          class: "graph-controller__info-text-background",
          "show-controls-graph": "",
          "show-latex-info": !0,
          "show-controls-environment": !1,
          "show-header": !0,
          "platform-type": Tr(s)
        }, null, 8, ["platform-type"])
      ], 512), [
        [br, !u.value]
      ])
    ], 64));
  }
});
/*! (c) Andrea Giammarchi - ISC */
const Km = (() => {
  const t = "DOMContentLoaded", e = /* @__PURE__ */ new WeakMap(), n = [], r = (o) => {
    do
      if (o.nextSibling)
        return !0;
    while (o = o.parentNode);
    return !1;
  }, i = () => {
    n.splice(0).forEach((o) => {
      e.get(o[0]) !== !0 && (e.set(o[0], !0), o[0][o[1]]());
    });
  };
  document.addEventListener(t, i);
  class s extends HTMLElement {
    static withParsedCallback(l, u = "parsed") {
      const { prototype: c } = l, { connectedCallback: a } = c, f = u + "Callback", h = (w, _, v, d) => {
        _.disconnect(), v.removeEventListener(t, d), p(w);
      }, p = (w) => {
        n.length || requestAnimationFrame(i), n.push([w, f]);
      };
      return Object.defineProperties(
        c,
        {
          connectedCallback: {
            configurable: !0,
            writable: !0,
            value() {
              if (a && a.apply(this, arguments), f in this && !e.has(this)) {
                const w = this, { ownerDocument: _ } = w;
                if (e.set(w, !1), _.readyState === "complete" || r(w))
                  p(w);
                else {
                  const v = () => h(w, d, _, v);
                  _.addEventListener(t, v);
                  const d = new MutationObserver(() => {
                    r(w) && h(w, d, _, v);
                  });
                  d.observe(w.parentNode, { childList: !0, subtree: !0 });
                }
              }
            }
          },
          [u]: {
            configurable: !0,
            get() {
              return e.get(this) === !0;
            }
          }
        }
      ), l;
    }
  }
  return s.withParsedCallback(s);
})();
/*! #__NO_SIDE_EFFECTS__ */
// @__NO_SIDE_EFFECTS__
function Xm(t, e, n) {
  const r = /* @__PURE__ */ fs(t);
  class i extends Ns {
    constructor(o) {
      super(r, o, e, n);
    }
  }
  return yt(i, "def", r), i;
}
const Ym = typeof HTMLElement < "u" ? Km : class {
};
class Ns extends Ym {
  constructor(n, r = {}, i = {}, s) {
    super();
    /**
     * @internal
     */
    yt(this, "_instance", null);
    yt(this, "_connected", !1);
    yt(this, "_resolved", !1);
    yt(this, "_numberProps", null);
    yt(this, "_styles");
    yt(this, "_slots");
    yt(this, "_ob", null);
    this._def = n, this._props = r, this._config = i, this._config = Tt(
      {
        shadowRoot: !0
      },
      this._config
    ), this._config.shadowRoot ? this.shadowRoot && s ? s(this._createVNode(), this._root) : (this.attachShadow({ mode: "open" }), this._def.__asyncLoader || this._resolveProps(this._def)) : s && s(this._createVNode(), this._root);
  }
  get _root() {
    return this._config.shadowRoot ? this.shadowRoot : this;
  }
  connectedCallback() {
    this._config.shadowRoot ? this._connect() : super.connectedCallback();
  }
  // use of parsedCallback when shadowRoot is disabled
  // to wait for slots to be parsed
  // see https://stackoverflow.com/a/52884370
  parsedCallback() {
    this._config.shadowRoot || this._connect();
  }
  _connect() {
    this._connected = !0, this._instance || (this._resolved ? this._update() : this._resolveDef());
  }
  disconnectedCallback() {
    this._connected = !1, this._ob && (this._ob.disconnect(), this._ob = null), yl(() => {
      this._connected || (po(null, this._root), this._instance = null);
    });
  }
  /**
   * resolve inner component definition (handle possible async component)
   */
  _resolveDef() {
    this._resolved = !0;
    for (let i = 0; i < this.attributes.length; i++)
      this._setAttr(this.attributes[i].name);
    this._ob = new MutationObserver((i) => {
      for (const s of i)
        this._setAttr(s.attributeName);
    }), this._ob.observe(this, { attributes: !0 });
    const n = (i, s = !1) => {
      const { props: o, styles: l } = i;
      let u;
      if (o && !rt(o))
        for (const c in o) {
          const a = o[c];
          (a === Number || a && a.type === Number) && (c in this._props && (this._props[c] = $s(this._props[c])), (u || (u = /* @__PURE__ */ Object.create(null)))[ke(c)] = !0);
        }
      this._numberProps = u, s && this._resolveProps(i), this._config.shadowRoot || (this._slots = Array.from(this.children).map((c) => c.cloneNode(!0)), this.replaceChildren()), this._applyStyles(l), this._update();
    }, r = this._def.__asyncLoader;
    r ? r().then((i) => n(i, !0)) : n(this._def);
  }
  _resolveProps(n) {
    const { props: r } = n, i = rt(r) ? r : Object.keys(r || {});
    for (const s of Object.keys(this))
      s[0] !== "_" && i.includes(s) && this._setProp(s, this[s], !0, !1);
    for (const s of i.map(ke))
      Object.defineProperty(this, s, {
        get() {
          return this._getProp(s);
        },
        set(o) {
          this._setProp(s, o);
        }
      });
  }
  _setAttr(n) {
    let r = this.getAttribute(n);
    const i = ke(n);
    this._numberProps && this._numberProps[i] && (r = $s(r)), this._setProp(i, r, !1);
  }
  /**
   * @internal
   */
  _getProp(n) {
    return this._props[n];
  }
  /**
   * @internal
   */
  _setProp(n, r, i = !0, s = !0) {
    r !== this._props[n] && (this._props[n] = r, s && this._instance && this._update(), i && (r === !0 ? this.setAttribute(ue(n), "") : typeof r == "string" || typeof r == "number" ? this.setAttribute(ue(n), r + "") : r || this.removeAttribute(ue(n))));
  }
  _update() {
    po(this._createVNode(), this._root);
  }
  _createVNode() {
    let n = null;
    this._config.shadowRoot || (n = () => {
      const i = (s) => {
        const o = {};
        for (let l = 0, u = s.length; l < u; l++) {
          const c = s[l];
          o[c.nodeName] = c.nodeValue;
        }
        return o;
      };
      return this._slots.map((s) => {
        const o = s.attributes ? i(s.attributes) : {};
        return o.innerHTML = s.innerHTML, _e(s.tagName, o, null);
      });
    });
    const r = _e(this._def, Tt({}, this._props), n);
    return this._instance || (r.ce = (i) => {
      this._instance = i, this._config.shadowRoot && (i.isCE = !0);
      const s = (l, u) => {
        this.dispatchEvent(
          new CustomEvent(l, {
            detail: u
          })
        );
      };
      i.emit = (l, ...u) => {
        s(l, u), ue(l) !== l && s(ue(l), u);
      };
      let o = this;
      for (; o = o && (o.parentNode || o.host); )
        if (o instanceof Ns) {
          i.parent = o._instance, i.provides = o._instance.provides;
          break;
        }
    }), r;
  }
  _applyStyles(n) {
    n && n.forEach((r) => {
      const i = document.createElement("style");
      i.textContent = r, this._root.appendChild(i);
    });
  }
}
customElements.define(
  "graph-component",
  // With LaTeX without ShadowRoot for MathJax to work
  /* @__PURE__ */ Xm(Wm, { shadowRoot: !1 })
  // With ShadowRoot without LaTeX
  // defineCustomElement(GraphEditor)
  /* for switching off the LaTeX control info background, in the graph editor template
  in the graph controls tag you can use :show-latex-info="true" */
);
