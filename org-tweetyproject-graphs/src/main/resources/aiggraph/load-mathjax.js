window.MathJax = {
    options: { enableMenu: false },
    tex: {
        inlineMath: [
            ['$', '$'],
            ['\\(', '\\)']
        ]
    },
    svg: {
        fontCache: 'global'
    }
}
;(function () {
    let script = document.createElement('script')
    script.src = 'https://cdn.jsdelivr.net/npm/mathjax@3/es5/tex-svg.js'
    script.async = true
    document.head.appendChild(script)
})()
