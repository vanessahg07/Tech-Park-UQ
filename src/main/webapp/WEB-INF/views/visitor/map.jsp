<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="co.edu.uq.techpark.ds.ListaEnlazada" %>
<%@ page import="co.edu.uq.techpark.model.Visitante" %>
<%@ page import="co.edu.uq.techpark.servlet.ServletDelMapa.DatosNodo" %>
<%@ page import="co.edu.uq.techpark.servlet.ServletDelMapa.DatosArista" %>
<%
    if (session == null || !"VISITOR".equals(session.getAttribute("role"))) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    ListaEnlazada<DatosNodo> nodes = (ListaEnlazada<DatosNodo>) request.getAttribute("nodes");
    ListaEnlazada<DatosArista> edges = (ListaEnlazada<DatosArista>) request.getAttribute("edges");
    ListaEnlazada<String>   route = (ListaEnlazada<String>)   request.getAttribute("route");
    Object rdObj = request.getAttribute("routeDistance");
    Double routeDistance = rdObj != null ? ((Number) rdObj).doubleValue() : null;
    String error = (String) request.getAttribute("error");
    Visitante visitanteNav = (Visitante) session.getAttribute("user");
    int notifCount = (visitanteNav != null && visitanteNav.getNotificacionesSinLeer() != null)
                     ? visitanteNav.getNotificacionesSinLeer().tamanio() : 0;
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Mapa del Parque</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
    <style>
        #parkSvg, #graphSvg { width:100%; height:520px; border-radius:14px; border:1px solid var(--card-border); display:block; }
        #graphSvg { background:var(--sand); display:none; }
        .mode-btn.active { background:var(--olive)!important; color:#fff!important; border-color:var(--olive)!important; }
        .route-result { background:rgba(96,108,56,0.08); border:1px solid rgba(96,108,56,0.2); border-radius:10px; padding:.9rem 1.1rem; margin-top:1rem; }
        #mapTooltip { position:fixed; background:var(--card-bg); border:1px solid var(--card-border); border-radius:10px; padding:8px 12px; font-size:.82rem; color:var(--carbon); pointer-events:none; box-shadow:var(--shadow-sm); display:none; z-index:9999; min-width:160px; }
    </style>
</head>
<body>
<nav class="navbar navbar-expand-lg">
  <div class="container-fluid">
    <a class="navbar-brand" href="${pageContext.request.contextPath}/dashboard">🎢 Tech-Park UQ</a>
    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navMain"><span class="navbar-toggler-icon"></span></button>
    <div class="collapse navbar-collapse" id="navMain">
      <ul class="navbar-nav me-auto mb-2 mb-lg-0">
        <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/dashboard">Inicio</a></li>
        <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/visitor/profile">Mi Perfil</a></li>
        <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/ticket/purchase">Tickets</a></li>
        <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/queue/status">Mi Cola</a></li>
        <li class="nav-item"><a class="nav-link active" href="${pageContext.request.contextPath}/map">Mapa</a></li>
        <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/visitor/history">Historial</a></li>
        <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/visitor/favorites">Favoritos</a></li>
        <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/visitor/notifications">Notificaciones
            <% if (notifCount > 0) { %><span class="badge bg-danger rounded-pill"><%= notifCount %></span><% } %>
          </a></li>
      </ul>
      <ul class="navbar-nav ms-auto">
        <li class="nav-item">
          <form method="post" action="${pageContext.request.contextPath}/auth" class="d-inline">
            <input type="hidden" name="action" value="logout">
            <button type="submit" class="btn btn-outline-secondary btn-sm">Cerrar sesión</button>
          </form>
        </li>
      </ul>
    </div>
  </div>
</nav>

<div id="mapTooltip"></div>

<div class="container-fluid py-4">
<div class="row g-4">
<div class="col-lg-8">

  <div class="d-flex justify-content-between align-items-center mb-3 animate-fade-up">
    <div>
      <h2 style="margin-bottom:.1rem;">Mapa del Parque</h2>
      <p style="margin:0;font-size:.88rem;">Explora las atracciones y calcula rutas óptimas</p>
    </div>
    <div class="btn-group">
      <button id="btnPlan"  class="btn btn-outline-secondary mode-btn active" onclick="setMode('plan')">🗺 Plano</button>
      <button id="btnGraph" class="btn btn-outline-secondary mode-btn"        onclick="setMode('graph')">🔗 Grafo</button>
    </div>
  </div>

  <% if (error != null) { %><div class="alert alert-danger mb-3"><%= error %></div><% } %>

  <div class="d-flex gap-2 mb-3 flex-wrap">
    <span class="badge badge-ACTIVE">● Activa</span>
    <span class="badge badge-MAINTENANCE">● Mantenimiento</span>
    <span class="badge badge-CLOSED">● Cerrada</span>
    <% if (route != null && !route.estaVacia()) { %>
    <span class="badge" style="background:var(--olive);color:#fff;">● Ruta calculada</span>
    <% } %>
  </div>

  <!-- PLANO SVG -->
  <svg id="parkSvg" viewBox="0 0 900 560" xmlns="http://www.w3.org/2000/svg">
    <defs>
      <pattern id="grass" patternUnits="userSpaceOnUse" width="20" height="20">
        <rect width="20" height="20" fill="#8fbc5a"/>
        <circle cx="5" cy="5" r="2" fill="#7aaa45" opacity=".4"/>
        <circle cx="15" cy="13" r="1.5" fill="#6a9a35" opacity=".3"/>
      </pattern>
      <filter id="nodeShadow" x="-30%" y="-30%" width="160%" height="160%">
        <feDropShadow dx="0" dy="3" stdDeviation="4" flood-color="rgba(40,54,24,0.4)"/>
      </filter>
      <filter id="routeGlow" x="-30%" y="-30%" width="160%" height="160%">
        <feDropShadow dx="0" dy="0" stdDeviation="6" flood-color="rgba(96,108,56,0.8)"/>
      </filter>
      <linearGradient id="lakeG" x1="0" y1="0" x2="1" y2="1">
        <stop offset="0%" stop-color="#7ec8e3"/>
        <stop offset="100%" stop-color="#5ba8c8"/>
      </linearGradient>
      <linearGradient id="zone1G" x1="0" y1="0" x2="1" y2="1">
        <stop offset="0%" stop-color="#c8e0a0"/>
        <stop offset="100%" stop-color="#a8c878"/>
      </linearGradient>
      <linearGradient id="zone2G" x1="0" y1="0" x2="1" y2="1">
        <stop offset="0%" stop-color="#a0c8e0"/>
        <stop offset="100%" stop-color="#78a8c8"/>
      </linearGradient>
      <linearGradient id="zone3G" x1="0" y1="0" x2="1" y2="1">
        <stop offset="0%" stop-color="#e0d0a0"/>
        <stop offset="100%" stop-color="#c8b078"/>
      </linearGradient>
    </defs>

    <!-- Fondo cesped -->
    <rect width="900" height="560" fill="url(#grass)" rx="14"/>
    <rect x="2" y="2" width="896" height="556" fill="none" stroke="#5a8a30" stroke-width="3" rx="13" stroke-dasharray="14,5"/>

    <!-- Zona 1: Aventura Extrema (arriba izquierda) -->
    <rect x="30" y="30" width="270" height="210" rx="16" fill="url(#zone1G)" stroke="#606C38" stroke-width="2.5" opacity=".92"/>
    <text x="165" y="52" text-anchor="middle" font-size="11" fill="#283618" font-weight="800" font-family="sans-serif">🎢 ZONA AVENTURA EXTREMA</text>

    <!-- Zona 2: Acuatica (arriba derecha) -->
    <rect x="600" y="30" width="270" height="210" rx="16" fill="url(#zone2G)" stroke="#2a6a8a" stroke-width="2.5" opacity=".92"/>
    <text x="735" y="52" text-anchor="middle" font-size="11" fill="#1a3a5a" font-weight="800" font-family="sans-serif">💧 ZONA ACUATICA</text>

    <!-- Zona 3: Familiar (abajo centro) -->
    <rect x="180" y="350" width="540" height="175" rx="16" fill="url(#zone3G)" stroke="#8a5a2a" stroke-width="2.5" opacity=".92"/>
    <text x="450" y="372" text-anchor="middle" font-size="11" fill="#4a2a0a" font-weight="800" font-family="sans-serif">🎡 ZONA FAMILIAR</text>

    <!-- Caminos -->
    <rect x="30" y="252" width="840" height="26" rx="13" fill="#d4c4a0" opacity=".9"/>
    <rect x="437" y="30" width="26" height="490" rx="13" fill="#d4c4a0" opacity=".9"/>
    <path d="M 165 240 Q 200 280 270 350" stroke="#d4c4a0" stroke-width="20" fill="none" stroke-linecap="round" opacity=".85"/>
    <path d="M 735 240 Q 700 280 630 350" stroke="#d4c4a0" stroke-width="20" fill="none" stroke-linecap="round" opacity=".85"/>

    <!-- Lago central -->
    <ellipse cx="450" cy="265" rx="70" ry="48" fill="url(#lakeG)" opacity=".88"/>
    <ellipse cx="450" cy="265" rx="70" ry="48" fill="none" stroke="#5ba8c8" stroke-width="2" opacity=".5"/>
    <text x="450" y="260" text-anchor="middle" font-size="18" opacity=".75">🏞</text>
    <text x="450" y="278" text-anchor="middle" font-size="8" fill="#2a6a8a" font-weight="700" font-family="sans-serif">Lago Central</text>

    <!-- Entrada -->
    <rect x="395" y="528" width="110" height="28" rx="7" fill="#BC6C25" opacity=".95"/>
    <text x="450" y="547" text-anchor="middle" font-size="10" fill="#fff" font-weight="700" font-family="sans-serif">🚪 ENTRADA PRINCIPAL</text>

    <!-- Arboles decorativos -->
    <text x="45"  y="295" font-size="18" opacity=".65">🌳</text>
    <text x="845" y="295" font-size="18" opacity=".65">🌳</text>
    <text x="45"  y="490" font-size="16" opacity=".55">🌲</text>
    <text x="845" y="490" font-size="16" opacity=".55">🌲</text>
    <text x="350" y="175" font-size="13" opacity=".5">🌿</text>
    <text x="520" y="175" font-size="13" opacity=".5">🌿</text>
    <text x="350" y="430" font-size="13" opacity=".5">🌿</text>
    <text x="560" y="430" font-size="13" opacity=".5">🌿</text>

    <!-- Capas dinamicas -->
    <g id="planEdges"></g>
    <g id="planNodes"></g>
  </svg>

  <!-- GRAFO SVG -->
  <svg id="graphSvg" viewBox="0 0 800 500" xmlns="http://www.w3.org/2000/svg">
    <defs>
      <pattern id="gridP" patternUnits="userSpaceOnUse" width="40" height="40">
        <path d="M 40 0 L 0 0 0 40" fill="none" stroke="rgba(96,108,56,0.1)" stroke-width="1"/>
      </pattern>
    </defs>
    <rect width="800" height="500" fill="url(#gridP)" rx="12"/>
    <g id="graphEdges"></g>
    <g id="graphNodes"></g>
  </svg>

  <!-- Resultado de ruta -->
  <% if (route != null && !route.estaVacia()) { %>
  <div class="route-result animate-fade-up">
    <strong style="color:var(--olive);">Ruta calculada:</strong>
    <span style="color:var(--carbon);margin-left:6px;">
    <% int _ri = 0;
       for (ListaEnlazada.Iterador<String> _rit = route.iterador(); _rit.tieneSiguiente(); _ri++) {
           String nid = _rit.siguiente();
           String nname = nid;
           if (nodes != null) {
               for (ListaEnlazada.Iterador<DatosNodo> _ni = nodes.iterador(); _ni.tieneSiguiente(); ) {
                   DatosNodo _nd = _ni.siguiente();
                   if (nid.equals(_nd.id)) { nname = _nd.nombre; break; }
               }
           }
    %><%= nname %><%= _rit.tieneSiguiente() ? " &rarr; " : "" %>
    <% } %>
    </span>
    <% if (routeDistance != null) { %>
    <span style="color:var(--terracotta);font-weight:700;margin-left:8px;">&middot; <%= routeDistance.intValue() %> m</span>
    <% } %>
  </div>
  <% } %>

</div><!-- col-lg-8 -->

<!-- Panel derecho -->
<div class="col-lg-4">
  <div class="card mb-3 animate-fade-up">
    <div class="card-header bg-primary"><h6 style="margin:0;color:#fff;">Buscar Ruta Óptima</h6></div>
    <div class="card-body">
      <form method="post" action="${pageContext.request.contextPath}/map/route">
        <div class="mb-3">
          <label class="form-label">Origen</label>
          <select class="form-select" name="origin" required>
            <option value="" disabled selected>Selecciona origen...</option>
            <% if (nodes != null) { for (ListaEnlazada.Iterador<DatosNodo> _ni = nodes.iterador(); _ni.tieneSiguiente(); ) { DatosNodo _nd = _ni.siguiente(); %>
            <option value="<%= _nd.id %>"><%= _nd.icono %> <%= _nd.nombre %></option>
            <% } } %>
          </select>
        </div>
        <div class="mb-3">
          <label class="form-label">Destino</label>
          <select class="form-select" name="destination" required>
            <option value="" disabled selected>Selecciona destino...</option>
            <% if (nodes != null) { for (ListaEnlazada.Iterador<DatosNodo> _ni2 = nodes.iterador(); _ni2.tieneSiguiente(); ) { DatosNodo _nd2 = _ni2.siguiente(); %>
            <option value="<%= _nd2.id %>"><%= _nd2.icono %> <%= _nd2.nombre %></option>
            <% } } %>
          </select>
        </div>
        <button type="submit" class="btn btn-primary w-100">Calcular ruta</button>
      </form>
    </div>
  </div>

  <% if (nodes != null && !nodes.estaVacia()) { %>
  <div class="card animate-fade-up">
    <div class="card-header" style="background:var(--sand)!important;"><h6 style="margin:0;color:var(--carbon);">Atracciones</h6></div>
    <div style="max-height:340px;overflow-y:auto;">
    <% for (ListaEnlazada.Iterador<DatosNodo> _nli = nodes.iterador(); _nli.tieneSiguiente(); ) { DatosNodo _nd3 = _nli.siguiente();
           String _ns = _nd3.estado;
           String _badgeClass = "badge-" + _ns;
    %>
    <div style="display:flex;justify-content:space-between;align-items:center;padding:.6rem 1rem;border-bottom:1px solid var(--card-border);">
      <div style="flex:1;">
        <div style="font-size:.87rem;font-weight:600;color:var(--carbon);"><%= _nd3.icono %> <%= _nd3.nombre %></div>
        <div style="font-size:.74rem;color:var(--carbon-muted);">Cola: <%= _nd3.cola %> &middot; ~<%= _nd3.espera %> min</div>
      </div>
      <div style="display:flex;gap:6px;align-items:center;">
        <span class="badge <%= _badgeClass %>" style="font-size:.68rem;"><%= _ns %></span>
        <form method="post" action="${pageContext.request.contextPath}/visitor/favorites/add" style="margin:0;">
          <input type="hidden" name="attractionId" value="<%= _nd3.id %>">
          <button type="submit" class="btn btn-sm btn-outline-warning" style="padding:2px 8px;font-size:.7rem;" title="Agregar a favoritos">⭐</button>
        </form>
      </div>
    </div>
    <% } %>
    </div>
  </div>
  <% } %>
</div>
</div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
// ── Data from server ──────────────────────────────────────────────────────────
const NODES = [
  <% if (nodes != null) {
     boolean _first = true;
     for (ListaEnlazada.Iterador<DatosNodo> _jni = nodes.iterador(); _jni.tieneSiguiente(); ) {
         DatosNodo _jn = _jni.siguiente();
         if (!_first) out.print(",");
         _first = false;
  %>
  { id:"<%=_jn.id%>", name:"<%=_jn.nombre.replace("\"","\\\"")%>", status:"<%=_jn.estado%>",
    icon:"<%=_jn.icono%>", x:<%=_jn.x%>, y:<%=_jn.y%>,
    queue:<%=_jn.cola%>, wait:<%=_jn.espera%> }
  <% } } %>
];
const EDGES = [
  <% if (edges != null) {
     boolean _efirst = true;
     for (ListaEnlazada.Iterador<DatosArista> _jei = edges.iterador(); _jei.tieneSiguiente(); ) {
         DatosArista _je = _jei.siguiente();
         if (!_efirst) out.print(",");
         _efirst = false;
  %>
  { from:"<%=_je.desde%>", to:"<%=_je.hasta%>", weight:<%=_je.peso%> }
  <% } } %>
];
const ROUTE = [<% if (route != null) {
    boolean _rfirst = true;
    for (ListaEnlazada.Iterador<String> _jri = route.iterador(); _jri.tieneSiguiente(); ) {
        if (!_rfirst) out.print(",");
        _rfirst = false;
        out.print("\"" + _jri.siguiente() + "\"");
    }
} %>];

// ── Color helpers ─────────────────────────────────────────────────────────────
const STATUS_COLOR = { ACTIVE:"#4a7c59", MAINTENANCE:"#c9a227", CLOSED:"#c0392b" };
const STATUS_STROKE = { ACTIVE:"#2d5a3d", MAINTENANCE:"#8a6f1a", CLOSED:"#8b1a1a" };

function isOnRoute(id) { return ROUTE.includes(id); }
function isEdgeOnRoute(from, to) {
  for (let i = 0; i < ROUTE.length - 1; i++) {
    if ((ROUTE[i]===from && ROUTE[i+1]===to) || (ROUTE[i]===to && ROUTE[i+1]===from)) return true;
  }
  return false;
}

// ── SVG namespace helper ──────────────────────────────────────────────────────
function svgEl(tag, attrs) {
  const el = document.createElementNS("http://www.w3.org/2000/svg", tag);
  for (const [k, v] of Object.entries(attrs)) el.setAttribute(k, v);
  return el;
}

// ── Render PLANO (park map) ───────────────────────────────────────────────────
function renderPlan() {
  const eG = document.getElementById("planEdges");
  const nG = document.getElementById("planNodes");
  eG.innerHTML = ""; nG.innerHTML = "";

  // Edges
  EDGES.forEach(e => {
    const fn = NODES.find(n => n.id === e.from);
    const tn = NODES.find(n => n.id === e.to);
    if (!fn || !tn) return;
    const onRoute = isEdgeOnRoute(e.from, e.to);
    const line = svgEl("line", {
      x1: fn.x, y1: fn.y, x2: tn.x, y2: tn.y,
      stroke: onRoute ? "#606C38" : "#b8a88a",
      "stroke-width": onRoute ? 5 : 3,
      "stroke-dasharray": onRoute ? "none" : "8,4",
      opacity: onRoute ? 1 : 0.7
    });
    eG.appendChild(line);

    // Weight label
    const mx = (fn.x + tn.x) / 2, my = (fn.y + tn.y) / 2;
    const bg = svgEl("rect", { x: mx-14, y: my-10, width: 28, height: 14, rx: 4,
      fill: onRoute ? "#606C38" : "#d4c4a0", opacity: 0.9 });
    const lbl = svgEl("text", { x: mx, y: my+1, "text-anchor":"middle",
      "font-size": 9, fill: onRoute ? "#fff" : "#5a4a2a", "font-weight":"600", "font-family":"sans-serif" });
    lbl.textContent = e.weight + "m";
    eG.appendChild(bg); eG.appendChild(lbl);
  });

  // Nodes
  NODES.forEach(n => {
    const onRoute = isOnRoute(n.id);
    const color = STATUS_COLOR[n.status] || "#888";
    const stroke = onRoute ? "#283618" : (STATUS_STROKE[n.status] || "#555");
    const r = onRoute ? 24 : 20;

    const g = svgEl("g", { cursor: "pointer", filter: onRoute ? "url(#routeGlow)" : "url(#nodeShadow)" });

    // Outer ring for route nodes
    if (onRoute) {
      g.appendChild(svgEl("circle", { cx: n.x, cy: n.y, r: r+5,
        fill: "none", stroke: "#606C38", "stroke-width": 3, opacity: 0.6 }));
    }

    g.appendChild(svgEl("circle", { cx: n.x, cy: n.y, r: r,
      fill: color, stroke: stroke, "stroke-width": onRoute ? 3 : 2 }));

    // Icon
    const icon = svgEl("text", { x: n.x, y: n.y - 3, "text-anchor":"middle",
      "font-size": 13, "dominant-baseline":"middle" });
    icon.textContent = n.icon;
    g.appendChild(icon);

    // Short name
    const shortName = n.name.length > 9 ? n.name.substring(0, 9) + "…" : n.name;
    const lbl = svgEl("text", { x: n.x, y: n.y + r + 12, "text-anchor":"middle",
      "font-size": 9, fill: "#283618", "font-weight":"700", "font-family":"sans-serif" });
    lbl.textContent = shortName;
    g.appendChild(lbl);

    // Tooltip events
    g.addEventListener("mouseenter", ev => showTooltip(ev, n));
    g.addEventListener("mousemove",  ev => moveTooltip(ev));
    g.addEventListener("mouseleave", hideTooltip);

    nG.appendChild(g);
  });
}

// ── Render GRAFO (force-like layout) ─────────────────────────────────────────
function renderGraph() {
  const eG = document.getElementById("graphEdges");
  const nG = document.getElementById("graphNodes");
  eG.innerHTML = ""; nG.innerHTML = "";

  // Use same x/y but scaled to 800x500 viewBox
  // Find bounding box
  if (NODES.length === 0) return;
  let minX = Infinity, maxX = -Infinity, minY = Infinity, maxY = -Infinity;
  NODES.forEach(n => { minX=Math.min(minX,n.x); maxX=Math.max(maxX,n.x); minY=Math.min(minY,n.y); maxY=Math.max(maxY,n.y); });
  const scaleX = (x) => 60 + (x - minX) / Math.max(maxX - minX, 1) * 680;
  const scaleY = (y) => 50 + (y - minY) / Math.max(maxY - minY, 1) * 400;

  // Edges
  EDGES.forEach(e => {
    const fn = NODES.find(n => n.id === e.from);
    const tn = NODES.find(n => n.id === e.to);
    if (!fn || !tn) return;
    const onRoute = isEdgeOnRoute(e.from, e.to);
    const sx = scaleX(fn.x), sy = scaleY(fn.y), tx = scaleX(tn.x), ty = scaleY(tn.y);
    eG.appendChild(svgEl("line", {
      x1: sx, y1: sy, x2: tx, y2: ty,
      stroke: onRoute ? "#606C38" : "#adb5bd",
      "stroke-width": onRoute ? 4 : 2
    }));
    const mx = (sx+tx)/2, my = (sy+ty)/2;
    const lbl = svgEl("text", { x: mx, y: my-5, "text-anchor":"middle",
      "font-size": 10, fill: onRoute ? "#606C38" : "#6c757d", "font-weight": onRoute ? "700" : "400" });
    lbl.textContent = e.weight + "m";
    eG.appendChild(lbl);
  });

  // Nodes
  NODES.forEach(n => {
    const onRoute = isOnRoute(n.id);
    const color = STATUS_COLOR[n.status] || "#888";
    const stroke = onRoute ? "#283618" : "#fff";
    const sx = scaleX(n.x), sy = scaleY(n.y);

    const g = svgEl("g", { cursor: "pointer" });
    g.appendChild(svgEl("circle", { cx: sx, cy: sy, r: 22,
      fill: color, stroke: stroke, "stroke-width": onRoute ? 4 : 2 }));

    const icon = svgEl("text", { x: sx, y: sy - 2, "text-anchor":"middle",
      "font-size": 14, "dominant-baseline":"middle" });
    icon.textContent = n.icon;
    g.appendChild(icon);

    const shortName = n.name.length > 10 ? n.name.substring(0, 10) + "…" : n.name;
    const lbl = svgEl("text", { x: sx, y: sy + 34, "text-anchor":"middle",
      "font-size": 10, fill: "#283618", "font-weight":"600", "font-family":"sans-serif" });
    lbl.textContent = shortName;
    g.appendChild(lbl);

    g.addEventListener("mouseenter", ev => showTooltip(ev, n));
    g.addEventListener("mousemove",  ev => moveTooltip(ev));
    g.addEventListener("mouseleave", hideTooltip);

    nG.appendChild(g);
  });
}

// ── Tooltip ───────────────────────────────────────────────────────────────────
const tooltip = document.getElementById("mapTooltip");
function showTooltip(ev, n) {
  const statusLabel = { ACTIVE:"Activa", MAINTENANCE:"Mantenimiento", CLOSED:"Cerrada" };
  const statusColor = { ACTIVE:"#4a7c59", MAINTENANCE:"#c9a227", CLOSED:"#c0392b" };
  tooltip.innerHTML =
    '<strong style="font-size:.9rem;">' + n.icon + ' ' + n.name + '</strong><br>' +
    '<span style="color:' + (statusColor[n.status]||"#888") + ';font-weight:600;">' + (statusLabel[n.status]||n.status) + '</span><br>' +
    '<span style="color:#6c757d;">Cola: ' + n.queue + ' · ~' + n.wait + ' min</span>';
  tooltip.style.display = "block";
  moveTooltip(ev);
}
function moveTooltip(ev) {
  tooltip.style.left = (ev.clientX + 14) + "px";
  tooltip.style.top  = (ev.clientY - 10) + "px";
}
function hideTooltip() { tooltip.style.display = "none"; }

// ── Mode toggle ───────────────────────────────────────────────────────────────
function setMode(mode) {
  document.getElementById("parkSvg").style.display  = mode === "plan"  ? "block" : "none";
  document.getElementById("graphSvg").style.display = mode === "graph" ? "block" : "none";
  document.getElementById("btnPlan").classList.toggle("active",  mode === "plan");
  document.getElementById("btnGraph").classList.toggle("active", mode === "graph");
}

// ── Init ──────────────────────────────────────────────────────────────────────
renderPlan();
renderGraph();
</script>
</body>
</html>
