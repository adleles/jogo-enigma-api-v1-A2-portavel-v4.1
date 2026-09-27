const express=require('express'); const app=express(); const API=process.env.API_URL||'http://localhost:8080'; app.use(express.json()); app.use(express.static('public'));
app.get('/bff/participantes',async(req,res)=>{const r=await fetch(`${API}/api/participantes`);res.status(r.status).send(await r.text());});
app.post('/bff/participantes',async(req,res)=>{const r=await fetch(`${API}/api/participantes`,{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify(req.body)});res.status(r.status).send(await r.text());});
app.get('/health',(req,res)=>res.json({status:'UP'})); app.listen(3000,()=>console.log('BFF :3000 -> '+API));
