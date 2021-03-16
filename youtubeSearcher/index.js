const ytsr = require('ytsr');


    (async () => {
        const inputQuery = process.argv.slice(2).join(" ")

        const filters = await ytsr.getFilters(inputQuery, {
            gl: "PL",
            hl: "pl",
        })
        const filter = filters.get('Typ').get('Materiał wideo')

        const searchResults = await ytsr(filter.url, {
            limit: 1,
            gl: "PL",
            hl: "pl",
            safeSearch: false
        })
        process.stdout.write(JSON.stringify(searchResults))
        process.exit(0)
    })();

